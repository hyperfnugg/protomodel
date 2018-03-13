package demo

import demo.Type.number
import demo.Type.text

enum class Type {
    number, text
}


data class Felt(
        val name: String,
        val type: Type,
        val xmlPath: String? = null,
        val xmlType: Type? = type,
        val temporal: Boolean = false
) : Selector {
    override fun select(verdier: List<Verdi>): Verdi? =
            verdier.find { it.felt == this }
}

val inntekt = Felt("inntekt", number, "inntekt", text)
val bolig_id = Felt("bolig.id", number, "id")
val bolig_prosentAndel = Felt("bolig.prosentAndel", number)
val konto_kontonummer = Felt("konto.kontonummer", number, "lanekonto", text)
val konto_saldo = Felt("konto.saldo", number, "lanesum")
val formue = Felt("formue", number)

val standardModell = listOf(inntekt, bolig_id, bolig_prosentAndel, konto_kontonummer, konto_saldo, formue)

interface Selector {
    fun select(verdier: List<Verdi>): Verdi?;
}

data class Verdi(
        val felt: Felt,
        val value: Any,
        val konto: Int? = null,
        val bolig: Int? = null
)


typealias Operation = (List<Verdi>) -> List<Verdi>

fun addVerdi(verdi: Verdi): Operation {
    return { verdier: List<Verdi> ->
        verdier.plus(verdi)
    }
}

val noop: Operation = { verdier: List<Verdi> -> verdier }


//fun andelFraBrøkTilProsent(tellerSelector: Selector, nevnerSelector: Selector): (List<Verdi>) -> Operation {
//    return { data: List<Verdi> ->
//        {
//            val teller = tellerSelector.select(data)
//            val nevner = nevnerSelector.select(data)
//            if (teller == null || nevner == null) {
//                noop
//            } else {
//                teller.value
//                        .let { it as Int }
//                        .let { it * 100 }
//                        .let { it / (nevner.value as Int) }
//                        .let {
//                            addVerdi(teller.copy(
//                                    felt = bolig_prosentAndel,
//                                    value = it
//
//                            ))
//                        }
//            }
//        }
//    }
//}


data class VerdiSett(val verdier: List<Verdi>)

data class Group(val retreiveId: (Verdi) -> Int?) {
//    fun forEach(fn: (VerdiSett) -> VerdiSett): (VerdiSett) -> VerdiSett {
//        val ff = (data) -> {
//            data.verdier
//                    .filter { verdi -> retreiveId(verdi) != null }
//                    .groupBy(retreiveId)
//                    .forEach((id, verdier) -> {
//            if (id == nll) {
//                verdier;
//            } else {
//                return
//            })
//        }
//        }
//
//        )
//
//    }
}


