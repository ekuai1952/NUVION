package kz.nuvion.app.data.demo

import kz.nuvion.app.domain.model.AtomModelType
import kz.nuvion.app.domain.model.Characteristic
import kz.nuvion.app.domain.model.LibraryIcon
import kz.nuvion.app.domain.model.LibraryMaterial

object DemoLibrary {

    private val MASS = L("Массасы", "Масса", "Mass")
    private val CHARGE = L("Заряды", "Заряд", "Charge")
    private val COMPOSITION = L("Құрамы", "Состав", "Composition")

    val all: List<LibraryMaterial> = listOf(
        LibraryMaterial(
            id = "proton",
            title = L("Протон", "Протон", "Proton"),
            icon = LibraryIcon.PROTON,
            available = true,
            definition = L(
                "Протон — атом ядросының құрамындағы оң зарядталған тұрақты бөлшек. Ядродағы протондар саны химиялық элементті анықтайды.",
                "Протон — стабильная положительно заряженная частица в составе атомного ядра. Число протонов определяет химический элемент.",
                "The proton is a stable, positively charged particle in the nucleus. The number of protons defines the chemical element."
            ),
            characteristics = listOf(
                Characteristic(MASS, L("1.6726·10⁻²⁷ кг", "1.6726·10⁻²⁷ кг", "1.6726·10⁻²⁷ kg")),
                Characteristic(CHARGE, L("+1.6·10⁻¹⁹ Кл", "+1.6·10⁻¹⁹ Кл", "+1.6·10⁻¹⁹ C")),
                Characteristic(COMPOSITION, L("u u d кварктары", "кварки u u d", "quarks u u d")),
                Characteristic(L("Спин", "Спин", "Spin"), same("1/2"))
            ),
            fact = L(
                "Протонның ыдырауы ешқашан байқалмаған: оның өмір сүру уақыты 10³⁴ жылдан асады.",
                "Распад протона ни разу не наблюдался: его время жизни больше 10³⁴ лет.",
                "Proton decay has never been observed: its lifetime exceeds 10³⁴ years."
            ),
            model = AtomModelType.PROTON
        ),
        LibraryMaterial(
            id = "neutron",
            title = L("Нейтрон", "Нейтрон", "Neutron"),
            icon = LibraryIcon.NEUTRON,
            available = true,
            definition = L(
                "Нейтрон — заряды жоқ ядролық бөлшек. Протондармен бірге нуклондар деп аталады.",
                "Нейтрон — электрически нейтральная частица ядра. Вместе с протонами их называют нуклонами.",
                "The neutron is an electrically neutral particle of the nucleus. Together with protons they are called nucleons."
            ),
            characteristics = listOf(
                Characteristic(MASS, L("1.6749·10⁻²⁷ кг", "1.6749·10⁻²⁷ кг", "1.6749·10⁻²⁷ kg")),
                Characteristic(CHARGE, same("0")),
                Characteristic(COMPOSITION, L("u d d кварктары", "кварки u d d", "quarks u d d")),
                Characteristic(
                    L("Еркін күйдегі өмір сүру уақыты", "Время жизни в свободном состоянии", "Free lifetime"),
                    L("≈ 880 с", "≈ 880 с", "≈ 880 s")
                )
            ),
            fact = L(
                "Нейтронды 1932 жылы ағылшын физигі Джеймс Чедвик ашты.",
                "Нейтрон открыл в 1932 году английский физик Джеймс Чедвик.",
                "The neutron was discovered in 1932 by the English physicist James Chadwick."
            ),
            model = AtomModelType.NEUTRON
        ),
        LibraryMaterial(
            id = "photon",
            title = L("Фотон", "Фотон", "Photon"),
            icon = LibraryIcon.PHOTON,
            available = true,
            definition = L(
                "Фотон — электромагниттік сәулеленудің кванты, жарық бөлшегі.",
                "Фотон — квант электромагнитного излучения, частица света.",
                "The photon is a quantum of electromagnetic radiation, a particle of light."
            ),
            characteristics = listOf(
                Characteristic(L("Тыныштық массасы", "Масса покоя", "Rest mass"), same("0")),
                Characteristic(CHARGE, same("0")),
                Characteristic(L("Жылдамдығы", "Скорость", "Speed"), L("c ≈ 3·10⁸ м/с", "c ≈ 3·10⁸ м/с", "c ≈ 3·10⁸ m/s")),
                Characteristic(L("Энергиясы", "Энергия", "Energy"), same("E = hν"))
            ),
            fact = L(
                "Күн ядросында пайда болған фотон бетке шығу үшін ондаған мың жыл жол жүреді.",
                "Фотону, рождённому в ядре Солнца, нужны десятки тысяч лет, чтобы выбраться на поверхность.",
                "A photon born in the Sun's core needs tens of thousands of years to reach the surface."
            ),
            model = AtomModelType.PHOTON
        ),
        LibraryMaterial(
            id = "isotope",
            title = L("Изотоп", "Изотоп", "Isotope"),
            icon = LibraryIcon.ISOTOPE,
            available = true,
            definition = L(
                "Изотоптар — протон саны бірдей, бірақ нейтрон саны әртүрлі бір элементтің атомдары.",
                "Изотопы — атомы одного элемента с одинаковым числом протонов, но разным числом нейтронов.",
                "Isotopes are atoms of one element with the same number of protons but different numbers of neutrons."
            ),
            characteristics = listOf(
                Characteristic(L("Протон саны (Z)", "Число протонов (Z)", "Protons (Z)"), L("бірдей", "одинаковое", "the same")),
                Characteristic(L("Нейтрон саны (N)", "Число нейтронов (N)", "Neutrons (N)"), L("әртүрлі", "разное", "different")),
                Characteristic(L("Сутегі изотоптары", "Изотопы водорода", "Hydrogen isotopes"), same("¹H, ²H, ³H"))
            ),
            fact = L(
                "Табиғи уранда ²³⁵U изотопы небәрі 0.7 % құрайды.",
                "В природном уране изотопа ²³⁵U всего 0.7 %.",
                "Natural uranium contains only 0.7 % of the ²³⁵U isotope."
            ),
            model = AtomModelType.ISOTOPE
        ),
        LibraryMaterial(
            id = "reactor",
            title = L("Реактор", "Реактор", "Reactor"),
            icon = LibraryIcon.REACTOR,
            available = true,
            definition = L(
                "Ядролық реактор — басқарылатын тізбекті бөліну реакциясы жүретін қондырғы.",
                "Ядерный реактор — установка, в которой идёт управляемая цепная реакция деления.",
                "A nuclear reactor is a facility where a controlled fission chain reaction takes place."
            ),
            characteristics = listOf(
                Characteristic(L("Отын", "Топливо", "Fuel"), same("²³⁵U, ²³⁹Pu")),
                Characteristic(L("Баяулатқыш", "Замедлитель", "Moderator"), L("су, графит", "вода, графит", "water, graphite")),
                Characteristic(L("Басқару", "Управление", "Control"), L("бор, кадмий стерженьдері", "стержни из бора, кадмия", "boron, cadmium rods"))
            ),
            fact = L(
                "Қазақстан уран өндіру бойынша әлемде бірінші орында.",
                "Казахстан занимает первое место в мире по добыче урана.",
                "Kazakhstan is the world's largest uranium producer."
            ),
            model = AtomModelType.REACTOR
        ),
        LibraryMaterial(
            id = "coming_soon",
            title = L("Жақында", "Скоро", "Coming soon"),
            icon = LibraryIcon.COMING_SOON,
            available = false
        )
    )
}
