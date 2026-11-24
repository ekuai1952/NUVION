package kz.nuvion.app.data.demo

import kz.nuvion.app.domain.model.LocalizedText
import kz.nuvion.app.domain.model.PracticeProblem

object DemoProblems {

    private const val PROBLEM_XP = 30

    private val TIMES = L("есе", "раз", "times")

    val all: List<PracticeProblem> = listOf(
        // Atomic models
        PracticeProblem(
            id = "p1", topicId = TopicIds.ATOM_MODELS,
            statement = L(
                "Ядроның радиусы 10⁻¹⁵ м, атомның радиусы 10⁻¹⁰ м. Атом ядродан неше есе үлкен?",
                "Радиус ядра 10⁻¹⁵ м, радиус атома 10⁻¹⁰ м. Во сколько раз атом больше ядра?",
                "The nucleus radius is 10⁻¹⁵ m and the atom radius is 10⁻¹⁰ m. How many times larger is the atom?"
            ),
            given = listOf(L("r(я) = 10⁻¹⁵ м", "r(я) = 10⁻¹⁵ м", "r(nucleus) = 10⁻¹⁵ m"), L("r(а) = 10⁻¹⁰ м", "r(а) = 10⁻¹⁰ м", "r(atom) = 10⁻¹⁰ m")),
            find = listOf(same("k = ?")),
            formula = L("k = r(а) / r(я)", "k = r(а) / r(я)", "k = r(atom) / r(nucleus)"),
            hint = L("Үлкен радиусты кішісіне бөліңіз.", "Разделите больший радиус на меньший.", "Divide the larger radius by the smaller one."),
            solution = listOf(
                same("k = 10⁻¹⁰ / 10⁻¹⁵"),
                same("k = 10⁵ = 100000")
            ),
            answer = 100000.0, relativeTolerance = 0.01, unit = TIMES, xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p2", topicId = TopicIds.ATOM_MODELS,
            statement = L(
                "Альфа-бөлшектің заряды екі элементар зарядқа тең. e = 1.6·10⁻¹⁹ Кл болса, оның зарядын табыңыз.",
                "Заряд альфа-частицы равен двум элементарным зарядам. Найдите его, если e = 1.6·10⁻¹⁹ Кл.",
                "An alpha particle carries two elementary charges. Find its charge if e = 1.6·10⁻¹⁹ C."
            ),
            given = listOf(same("e = 1.6·10⁻¹⁹ Кл (C)"), same("q = 2e")),
            find = listOf(same("q = ?")),
            formula = same("q = 2 · e"),
            hint = L("Элементар зарядты екіге көбейтіңіз.", "Умножьте элементарный заряд на два.", "Multiply the elementary charge by two."),
            solution = listOf(same("q = 2 · 1.6·10⁻¹⁹"), same("q = 3.2·10⁻¹⁹ Кл (C)")),
            answer = 3.2, relativeTolerance = 0.01,
            unit = L("×10⁻¹⁹ Кл", "×10⁻¹⁹ Кл", "×10⁻¹⁹ C"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p3", topicId = TopicIds.ATOM_MODELS,
            statement = L(
                "Протон массасы 1.67·10⁻²⁷ кг, электрон массасы 9.11·10⁻³¹ кг. Протон электроннан неше есе ауыр?",
                "Масса протона 1.67·10⁻²⁷ кг, масса электрона 9.11·10⁻³¹ кг. Во сколько раз протон тяжелее электрона?",
                "A proton has mass 1.67·10⁻²⁷ kg and an electron 9.11·10⁻³¹ kg. How many times heavier is the proton?"
            ),
            given = listOf(same("mₚ = 1.67·10⁻²⁷ кг (kg)"), same("mₑ = 9.11·10⁻³¹ кг (kg)")),
            find = listOf(same("mₚ / mₑ = ?")),
            formula = same("k = mₚ / mₑ"),
            hint = L(
                "Дәрежелерді бөлек бөліңіз: 10⁻²⁷ / 10⁻³¹ = 10⁴.",
                "Поделите степени отдельно: 10⁻²⁷ / 10⁻³¹ = 10⁴.",
                "Divide the powers separately: 10⁻²⁷ / 10⁻³¹ = 10⁴."
            ),
            solution = listOf(same("k = (1.67 / 9.11) · 10⁴"), same("k ≈ 0.1833 · 10⁴ ≈ 1833")),
            answer = 1833.0, relativeTolerance = 0.01, unit = TIMES, xpReward = PROBLEM_XP
        ),
        // Bohr
        PracticeProblem(
            id = "p1", topicId = TopicIds.BOHR,
            statement = L(
                "Сутегі атомының бірінші орбитасының радиусы 0.53 Å. Екінші орбитаның радиусын табыңыз.",
                "Радиус первой орбиты атома водорода 0.53 Å. Найдите радиус второй орбиты.",
                "The radius of hydrogen's first orbit is 0.53 Å. Find the radius of the second orbit."
            ),
            given = listOf(same("r₁ = 0.53 Å"), same("n = 2")),
            find = listOf(same("r₂ = ?")),
            formula = same("rₙ = n²r₁"),
            hint = L("n = 2 болғанда n² = 4.", "При n = 2 получаем n² = 4.", "For n = 2, n² = 4."),
            solution = listOf(same("r₂ = 2² · 0.53 Å"), same("r₂ = 4 · 0.53 = 2.12 Å")),
            answer = 2.12, relativeTolerance = 0.01, unit = same("Å"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p2", topicId = TopicIds.BOHR,
            statement = L(
                "Сутегі атомының үшінші деңгейінің энергиясын табыңыз.",
                "Найдите энергию третьего уровня атома водорода.",
                "Find the energy of the third level of hydrogen."
            ),
            given = listOf(same("E₁ = −13.6 эВ (eV)"), same("n = 3")),
            find = listOf(same("E₃ = ?")),
            formula = same("Eₙ = E₁ / n²"),
            hint = L("Энергия теріс болып қалады.", "Энергия остаётся отрицательной.", "The energy stays negative."),
            solution = listOf(same("E₃ = −13.6 / 3²"), same("E₃ = −13.6 / 9 ≈ −1.51 эВ (eV)")),
            answer = -1.51, relativeTolerance = 0.02, unit = L("эВ", "эВ", "eV"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p3", topicId = TopicIds.BOHR,
            statement = L(
                "Электрон n = 3 деңгейінен n = 2 деңгейіне өтті. Шығарылған фотонның энергиясын табыңыз.",
                "Электрон перешёл с уровня n = 3 на уровень n = 2. Найдите энергию испущенного фотона.",
                "An electron drops from n = 3 to n = 2. Find the energy of the emitted photon."
            ),
            given = listOf(same("E₃ = −1.51 эВ (eV)"), same("E₂ = −3.4 эВ (eV)")),
            find = listOf(same("hν = ?")),
            formula = same("hν = E₃ − E₂"),
            hint = L("Жоғарғы деңгейден төменгісін алыңыз.", "Вычтите энергию нижнего уровня из верхнего.", "Subtract the lower level from the upper one."),
            solution = listOf(same("hν = −1.51 − (−3.4)"), same("hν = 1.89 эВ (eV)")),
            answer = 1.89, relativeTolerance = 0.02, unit = L("эВ", "эВ", "eV"), xpReward = PROBLEM_XP
        ),
        // Radioactivity
        PracticeProblem(
            id = "p1", topicId = TopicIds.RADIOACTIVITY,
            statement = L(
                "Бастапқыда 1000 радиоактивті ядро болды. Жартылай ыдырау периоды 2 сағат. 6 сағаттан кейін қанша ядро қалады?",
                "Сначала было 1000 радиоактивных ядер. Период полураспада 2 часа. Сколько ядер останется через 6 часов?",
                "There are 1000 radioactive nuclei. The half-life is 2 hours. How many remain after 6 hours?"
            ),
            given = listOf(same("N₀ = 1000"), L("T = 2 сағ", "T = 2 ч", "T = 2 h"), L("t = 6 сағ", "t = 6 ч", "t = 6 h")),
            find = listOf(same("N = ?")),
            formula = same("N = N₀ · 2^(−t/T)"),
            hint = L("t/T = 3, демек саны үш рет екі есе азаяды.", "t/T = 3, значит число ядер трижды уменьшится вдвое.", "t/T = 3, so the number halves three times."),
            solution = listOf(same("N = 1000 · 2⁻³"), same("N = 1000 / 8 = 125")),
            answer = 125.0, relativeTolerance = 0.005, unit = L("ядро", "ядер", "nuclei"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p2", topicId = TopicIds.RADIOACTIVITY,
            statement = L(
                "Радий-226 изотопының жартылай ыдырау периоды 1600 жыл. 3200 жылдан кейін бастапқы ядролардың неше пайызы қалады?",
                "Период полураспада радия-226 равен 1600 лет. Сколько процентов ядер останется через 3200 лет?",
                "Radium-226 has a half-life of 1600 years. What percentage of nuclei remains after 3200 years?"
            ),
            given = listOf(L("T = 1600 жыл", "T = 1600 лет", "T = 1600 years"), L("t = 3200 жыл", "t = 3200 лет", "t = 3200 years")),
            find = listOf(same("N / N₀ = ? %")),
            formula = same("N / N₀ = 2^(−t/T)"),
            hint = L("Екі жартылай ыдырау периоды өтті.", "Прошло два периода полураспада.", "Two half-lives have passed."),
            solution = listOf(same("N / N₀ = 2⁻² = 1/4"), same("1/4 = 25 %")),
            answer = 25.0, relativeTolerance = 0.01, unit = same("%"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p3", topicId = TopicIds.RADIOACTIVITY,
            statement = L(
                "²³⁸U ядросы альфа-ыдырауға ұшырады. Пайда болған ядроның массалық саны қандай?",
                "Ядро ²³⁸U испытало альфа-распад. Каково массовое число образовавшегося ядра?",
                "A ²³⁸U nucleus undergoes alpha decay. What is the mass number of the new nucleus?"
            ),
            given = listOf(same("A = 238"), same("α = ⁴₂He")),
            find = listOf(same("A' = ?")),
            formula = same("A' = A − 4"),
            hint = L("Альфа-бөлшек 4 нуклон алып кетеді.", "Альфа-частица уносит 4 нуклона.", "An alpha particle carries away 4 nucleons."),
            solution = listOf(same("A' = 238 − 4"), same("A' = 234 (²³⁴Th)")),
            answer = 234.0, relativeTolerance = 0.001, unit = LocalizedText.Empty, xpReward = PROBLEM_XP
        ),
        // Nuclear reactions
        PracticeProblem(
            id = "p1", topicId = TopicIds.REACTIONS,
            statement = L(
                "Реакцияның масса ақауы 0.2 а.м.б. Бөлінетін энергияны МэВ-пен табыңыз (1 а.м.б. ≈ 931.5 МэВ).",
                "Дефект массы реакции 0.2 а.е.м. Найдите выделившуюся энергию в МэВ (1 а.е.м. ≈ 931.5 МэВ).",
                "The mass defect of a reaction is 0.2 u. Find the released energy in MeV (1 u ≈ 931.5 MeV)."
            ),
            given = listOf(L("Δm = 0.2 а.м.б.", "Δm = 0.2 а.е.м.", "Δm = 0.2 u"), L("1 а.м.б. = 931.5 МэВ", "1 а.е.м. = 931.5 МэВ", "1 u = 931.5 MeV")),
            find = listOf(same("E = ?")),
            formula = same("E = Δm · 931.5"),
            hint = L("Масса ақауын 931.5-ке көбейтіңіз.", "Умножьте дефект массы на 931.5.", "Multiply the mass defect by 931.5."),
            solution = listOf(same("E = 0.2 · 931.5"), L("E = 186.3 МэВ", "E = 186.3 МэВ", "E = 186.3 MeV")),
            answer = 186.3, relativeTolerance = 0.01, unit = L("МэВ", "МэВ", "MeV"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p2", topicId = TopicIds.REACTIONS,
            statement = L(
                "1 г (10⁻³ кг) масса толық энергияға айналса, қанша энергия бөлінеді? c = 3·10⁸ м/с.",
                "Какая энергия выделится, если 1 г (10⁻³ кг) массы полностью превратится в энергию? c = 3·10⁸ м/с.",
                "How much energy is released if 1 g (10⁻³ kg) of mass fully converts to energy? c = 3·10⁸ m/s."
            ),
            given = listOf(same("Δm = 10⁻³ кг (kg)"), same("c = 3·10⁸ м/с (m/s)")),
            find = listOf(same("E = ?")),
            formula = same("E = Δm · c²"),
            hint = L("c² = 9·10¹⁶ м²/с².", "c² = 9·10¹⁶ м²/с².", "c² = 9·10¹⁶ m²/s²."),
            solution = listOf(same("E = 10⁻³ · (3·10⁸)²"), L("E = 9·10¹³ Дж", "E = 9·10¹³ Дж", "E = 9·10¹³ J")),
            answer = 9.0, relativeTolerance = 0.01, unit = L("×10¹³ Дж", "×10¹³ Дж", "×10¹³ J"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p3", topicId = TopicIds.REACTIONS,
            statement = L(
                "²³⁵U + n → ¹⁴¹Ba + ⁹²Kr + x·n реакциясындағы нейтрондар санын (x) табыңыз.",
                "Найдите число нейтронов x в реакции ²³⁵U + n → ¹⁴¹Ba + ⁹²Kr + x·n.",
                "Find the number of neutrons x in ²³⁵U + n → ¹⁴¹Ba + ⁹²Kr + x·n."
            ),
            given = listOf(same("235 + 1 = 236"), same("141 + 92 = 233")),
            find = listOf(same("x = ?")),
            formula = same("235 + 1 = 141 + 92 + x"),
            hint = L("Массалық сан сақталады.", "Массовое число сохраняется.", "The mass number is conserved."),
            solution = listOf(same("x = 236 − 233"), same("x = 3")),
            answer = 3.0, relativeTolerance = 0.001, unit = L("нейтрон", "нейтрона", "neutrons"), xpReward = PROBLEM_XP
        ),
        // Reactor physics
        PracticeProblem(
            id = "p1", topicId = TopicIds.REACTOR,
            statement = L(
                "Көбею коэффициенті k = 2. Бір нейтроннан басталған тізбекте 10 буыннан кейін неше нейтрон болады?",
                "Коэффициент размножения k = 2. Сколько нейтронов будет через 10 поколений, если цепь началась с одного?",
                "The multiplication factor is k = 2. How many neutrons are there after 10 generations, starting from one?"
            ),
            given = listOf(same("k = 2"), same("N₀ = 1"), same("i = 10")),
            find = listOf(same("N₁₀ = ?")),
            formula = same("Nᵢ = N₀ · kⁱ"),
            hint = L("2¹⁰ есептеңіз.", "Вычислите 2¹⁰.", "Compute 2¹⁰."),
            solution = listOf(same("N₁₀ = 1 · 2¹⁰"), same("N₁₀ = 1024")),
            answer = 1024.0, relativeTolerance = 0.001, unit = L("нейтрон", "нейтронов", "neutrons"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p2", topicId = TopicIds.REACTOR,
            statement = L(
                "Реактордың жылу қуаты 1000 МВт. Бір бөліну 3.2·10⁻¹¹ Дж береді. Секундына неше бөліну болады?",
                "Тепловая мощность реактора 1000 МВт. Одно деление даёт 3.2·10⁻¹¹ Дж. Сколько делений происходит за секунду?",
                "A reactor's thermal power is 1000 MW. One fission gives 3.2·10⁻¹¹ J. How many fissions occur per second?"
            ),
            given = listOf(same("P = 10⁹ Вт (W)"), same("E₁ = 3.2·10⁻¹¹ Дж (J)")),
            find = listOf(same("N / t = ?")),
            formula = same("N / t = P / E₁"),
            hint = L("10⁹ / 3.2·10⁻¹¹ есептеңіз.", "Вычислите 10⁹ / 3.2·10⁻¹¹.", "Compute 10⁹ / 3.2·10⁻¹¹."),
            solution = listOf(same("N / t = 10⁹ / 3.2·10⁻¹¹"), same("N / t ≈ 3.125·10¹⁹ с⁻¹ (s⁻¹)")),
            answer = 3.125, relativeTolerance = 0.02, unit = L("×10¹⁹ с⁻¹", "×10¹⁹ с⁻¹", "×10¹⁹ s⁻¹"), xpReward = PROBLEM_XP
        ),
        PracticeProblem(
            id = "p3", topicId = TopicIds.REACTOR,
            statement = L(
                "АЭС-тің жылу қуаты 3000 МВт, ПӘК-і 33 %. Электр қуатын табыңыз.",
                "Тепловая мощность АЭС 3000 МВт, КПД 33 %. Найдите электрическую мощность.",
                "A nuclear plant has 3000 MW of thermal power and 33 % efficiency. Find its electrical power."
            ),
            given = listOf(L("P(жылу) = 3000 МВт", "P(тепл) = 3000 МВт", "P(th) = 3000 MW"), same("η = 33 %")),
            find = listOf(L("P(эл) = ?", "P(эл) = ?", "P(el) = ?")),
            formula = L("P(эл) = η · P(жылу)", "P(эл) = η · P(тепл)", "P(el) = η · P(th)"),
            hint = L("33 % = 0.33.", "33 % = 0.33.", "33 % = 0.33."),
            solution = listOf(same("P = 0.33 · 3000"), L("P = 990 МВт", "P = 990 МВт", "P = 990 MW")),
            answer = 990.0, relativeTolerance = 0.01, unit = L("МВт", "МВт", "MW"), xpReward = PROBLEM_XP
        )
    )
}

