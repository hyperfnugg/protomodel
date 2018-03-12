package demo

data class Melding(val inntekt: String, val boliger: List<Bolig>)

data class Bolig(val id: Int, val eierandel: Eierandel, val lanekonto: String, val lanesum: Int)

data class Eierandel(val teller: Int, val nevner: Int)

val boligTeller = Felt("bolig.teller", Type.number, "bolig[boligId].eierandel.teller", temporal = true)
val boligNevner = Felt("bolig.nevner", Type.number, "bolig[boligId].eierandel.nevner", temporal = true)


fun beanToVerdiSett(bean: Melding): VerdiSett {

    val felter = standardModell
            .filter { it.xmlPath != null }
            .plus(boligNevner)
            .plus(boligTeller)

    val verdier = felter.map { verdierForFelt(it, bean) }
    return VerdiSett(verdier)

}

fun verdierForFelt(felt: Felt, bean: Melding): Verdi {
    val path = felt.xmlPath!!.split('.')
    return Verdi(felt, 55)

}
