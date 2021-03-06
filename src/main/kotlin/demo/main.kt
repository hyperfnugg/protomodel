package demo

fun main(args: Array<String>) {
    val melding = Melding(
            inntekt = "5000",
            boliger = listOf(
                    Bolig(
                            id = 555,
                            eierandel = Eierandel(teller = 20, nevner = 50),
                            lanekonto = "555 1223",
                            lanesum = 100000
                    ),
                    Bolig(
                            id = 700,
                            eierandel = Eierandel(teller = 10, nevner = 50),
                            lanekonto = "666 2115",
                            lanesum = 200000
                    )
            )
    )
    val verdiSett = beanToVerdiSett(melding)
    println(verdiSett.toString())
    val bean = verdiSettToBean(verdiSett)
    println(bean.toString())
}
