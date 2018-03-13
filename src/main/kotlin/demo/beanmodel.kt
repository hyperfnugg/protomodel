package demo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

data class Melding(val inntekt: String, val boliger: List<Bolig>)

data class Bolig(val id: Int, val eierandel: Eierandel, val lanekonto: String, val lanesum: Int)

data class Eierandel(val teller: Int, val nevner: Int)

val boligTeller = Felt("bolig.teller", Type.number, "eierandel.teller", temporal = true)
val boligNevner = Felt("bolig.nevner", Type.number, "eierandel.nevner", temporal = true)

val mapper = ObjectMapper().registerModule(KotlinModule())

data class IdMeta(val felt: Felt, val settIVerdi: (Verdi, Int) -> Verdi, val hentFraVerdi: (Verdi) -> Int?) {
}

data class BeanGroup(
        val id: IdMeta? = null,
        val beanPath: String = "",
        val felter: List<Felt> = listOf(),
        val grupper: List<BeanGroup> = listOf()
)

val bolig = BeanGroup(
        id = IdMeta(
                felt = bolig_id,
                settIVerdi = { verdi, id -> verdi.copy(bolig = id) },
                hentFraVerdi = { it.bolig }

        ),
        beanPath = "boliger",
        felter = listOf(bolig_id, konto_kontonummer, konto_saldo, boligTeller, boligNevner)
)

val root = BeanGroup(
        felter = listOf(inntekt),
        grupper = listOf(bolig)
)

fun beanToVerdiSett(bean: Melding): VerdiSett =
        mapper.valueToTree<JsonNode>(bean)
                .let { verdierForGruppe(root, it) }
                .let { VerdiSett(it) }


fun verdierForGruppe(gruppe: BeanGroup, bean: JsonNode): List<Verdi> {
    val fraFelter = gruppe.felter.mapNotNull { feltTilVerdi(it, bean) }
    val fraGrupper = gruppe.grupper.flatMap { indreGruppe ->
        getNestedProp(bean, indreGruppe.beanPath)
                ?.flatMap { noe -> verdierForGruppe(indreGruppe, noe) }
                ?: listOf()
    }
    val ret = fraFelter.plus(fraGrupper)
    return if(gruppe.id != null) {
        val idFelt = ret.find { it.felt == gruppe.id.felt }
        ret.map { gruppe.id.settIVerdi(it, idFelt!!.value as Int) }
    } else {
        ret
    }
}

fun feltTilVerdi(felt: Felt, bean: JsonNode): Verdi? =
        getNestedProp(bean, felt.xmlPath!!)
                ?.let { beanVerdiTilFeltVerdi(it, felt) }
                ?.let { Verdi(felt, it) }

fun getNestedProp(node: JsonNode, path: String): JsonNode? =
        path.split('.').fold(
                node, { innerNode: JsonNode?, nextStep: String -> innerNode?.get(nextStep) })

fun beanVerdiTilFeltVerdi(verdi: JsonNode, felt: Felt): Any {
    if (felt.type == Type.number) {
        return verdi.asText()
                .trim()
                .replace(" ", "")
                .replace("\"", "")
                .toInt()
    } else {
        return verdi.asText()
    }
}

fun verdiSettToBean(verdiSett: VerdiSett): Melding {
    val f = mapOf(
            "inntekt" to "5000",
            "boliger" to listOf<Any>()

    )
    val gruppeTilBean = gruppeTilBean(root, verdiSett.verdier)
    return mapper.convertValue<Melding>(gruppeTilBean, Melding::class.java)
}

fun gruppeTilBean(gruppe: BeanGroup, verdier: List<Verdi>): Any? {
    return if (gruppe.id != null) {
        verdier
                .filter { gruppe.id.hentFraVerdi(it) != null }
                .groupBy { gruppe.id.hentFraVerdi(it) }
                .values
                .map { filtrertGruppeToBean(gruppe, it) }
//                .let { mapOf(gruppe.beanPath to it) }
    } else {
        filtrertGruppeToBean(gruppe, verdier)
//                .let { mapOf(gruppe.beanPath to it) }

    }
}

fun filtrertGruppeToBean(gruppe: BeanGroup, verdier: List<Verdi>): Map<String, Any?> {
    val forFelt = gruppe.felter.map { felt ->
        Pair(
                felt.xmlPath!!.split('.'),
                verdier.find { it.felt == felt }?.value
        )
    }
    val forGrupper = gruppe.grupper.map { indreGruppe ->
        Pair(
                indreGruppe.beanPath.split('.'),
                gruppeTilBean(indreGruppe, verdier)
        )
    }
    return fiksNestedePather(forFelt.plus(forGrupper))

}

fun fiksNestedePather(noder: List<Pair<List<String>, Any?>>): Map<String, Any?> {
    val levelOne = noder
            .filter { it.first.size <= 1 }
            .map { Pair(it.first[0], it.second) }
            .toMap()


    val levelNext = noder.filter { it.first.size > 1 }
            .groupBy { it.first[0] }
            .map { (key, value) ->
                Pair(
                        key,
                        fiksNestedePather(
                                value.map { (first, second) -> Pair(first.drop(1), second) }
                        )
                )
            }
            .toMap()

    return levelOne.plus(levelNext)

}

