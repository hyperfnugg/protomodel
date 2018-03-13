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
)

val inntekt = Felt("inntekt", number, "inntekt", text)
val bolig_id = Felt("bolig.id", number, "id")
val bolig_prosentAndel = Felt("bolig.prosentAndel", number)
val konto_kontonummer = Felt("konto.kontonummer", number, "lanekonto", text)
val konto_saldo = Felt("konto.saldo", number, "lanesum")
val formue = Felt("formue", number)

val standardModell = listOf(inntekt, bolig_id, bolig_prosentAndel, konto_kontonummer, konto_saldo, formue)


data class Verdi(
        val felt: Felt,
        val value: Any,
        val konto: Int? = null,
        val bolig: Int? = null
)

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


