package kz.nuvion.app.data.demo

import kz.nuvion.app.domain.model.AnswerOption
import kz.nuvion.app.domain.model.LocalizedText
import kz.nuvion.app.domain.model.Question
import kz.nuvion.app.domain.model.Quiz

object DemoQuizzes {

    private const val QUIZ_XP = 100
    private const val PASS_THRESHOLD = 0.6f

    /** [options] are listed in display order; [correct] is the index of the right one. */
    private fun q(
        id: String,
        text: LocalizedText,
        correct: Int,
        options: List<LocalizedText>,
        explanation: LocalizedText
    ) = Question(
        id = id,
        text = text,
        options = options.mapIndexed { index, option ->
            AnswerOption(id = "$id-$index", text = option, isCorrect = index == correct)
        },
        explanation = explanation
    )

    val all: List<Quiz> = listOf(
        Quiz(
            TopicIds.ATOM_MODELS, QUIZ_XP, PASS_THRESHOLD, listOf(
                q(
                    "am1",
                    L("Томсон моделінде атом қалай қарастырылады?", "Как рассматривается атом в модели Томсона?", "How is the atom described in Thomson's model?"),
                    0,
                    listOf(
                        L("Оң зарядты «науадағы» электрондар", "Электроны в положительно заряженной «массе»", "Electrons inside a positive \"pudding\""),
                        L("Планета жүйесі", "Планетарная система", "A planetary system"),
                        L("Бос кеңістік", "Пустое пространство", "Empty space"),
                        L("Кванттық модель", "Квантовая модель", "A quantum model")
                    ),
                    L(
                        "Томсон атомды электрондар біркелкі таралған оң зарядталған шар ретінде сипаттады («мейізді пудинг»).",
                        "Томсон представлял атом положительно заряженным шаром с вкраплёнными электронами («пудинг с изюмом»).",
                        "Thomson pictured a positively charged sphere with embedded electrons (the \"plum pudding\")."
                    )
                ),
                q(
                    "am2",
                    L("Резерфорд тәжірибесінде қандай бөлшектер қолданылды?", "Какие частицы использовал Резерфорд в своём опыте?", "Which particles did Rutherford use in his experiment?"),
                    1,
                    listOf(
                        L("Электрондар", "Электроны", "Electrons"),
                        L("Альфа-бөлшектер", "Альфа-частицы", "Alpha particles"),
                        L("Нейтрондар", "Нейтроны", "Neutrons"),
                        L("Фотондар", "Фотоны", "Photons")
                    ),
                    L(
                        "Алтын фольга альфа-бөлшектермен атқыланды.",
                        "Золотую фольгу обстреливали альфа-частицами.",
                        "Gold foil was bombarded with alpha particles."
                    )
                ),
                q(
                    "am3",
                    L("Резерфорд тәжірибесінің басты қорытындысы қандай?", "Каков главный вывод опыта Резерфорда?", "What was the main conclusion of Rutherford's experiment?"),
                    2,
                    listOf(
                        L("Электрондар ядрода орналасқан", "Электроны находятся в ядре", "Electrons are inside the nucleus"),
                        L("Атом бөлінбейді", "Атом неделим", "The atom is indivisible"),
                        L("Атомның массасы мен оң заряды кішкентай ядрода", "Масса и положительный заряд атома — в малом ядре", "The mass and positive charge are in a tiny nucleus"),
                        L("Атом біртекті", "Атом однороден", "The atom is uniform")
                    ),
                    L(
                        "Сирек кездесетін үлкен ауытқулар тығыз шағын ядро бар екенін көрсетті.",
                        "Редкие отклонения на большие углы указали на плотное малое ядро.",
                        "Rare large-angle deflections pointed to a dense, tiny nucleus."
                    )
                ),
                q(
                    "am4",
                    L("Резерфорд моделінің негізгі кемшілігі неде?", "В чём главный недостаток модели Резерфорда?", "What is the main flaw of Rutherford's model?"),
                    0,
                    listOf(
                        L("Классикалық физика бойынша электрон ядроға құлауы керек", "По классической физике электрон должен упасть на ядро", "Classically the electron should spiral into the nucleus"),
                        L("Модельде ядро жоқ", "В модели нет ядра", "The model has no nucleus"),
                        L("Модельде электрондар жоқ", "В модели нет электронов", "The model has no electrons"),
                        L("Масса біркелкі таралған", "Масса распределена равномерно", "Mass is spread evenly")
                    ),
                    L(
                        "Үдеумен қозғалған электрон энергия шығарып, ядроға құлауы тиіс еді — бірақ атомдар тұрақты.",
                        "Ускоренно движущийся электрон должен излучать и падать на ядро — но атомы устойчивы.",
                        "An accelerating electron should radiate and fall in — yet atoms are stable."
                    )
                ),
                q(
                    "am5",
                    L("Атом ядросының өлшемі шамамен қандай?", "Каков примерный размер атомного ядра?", "What is the approximate size of a nucleus?"),
                    3,
                    listOf(
                        L("10⁻¹⁰ м", "10⁻¹⁰ м", "10⁻¹⁰ m"),
                        L("10⁻⁵ м", "10⁻⁵ м", "10⁻⁵ m"),
                        L("1 мм", "1 мм", "1 mm"),
                        L("10⁻¹⁵ м", "10⁻¹⁵ м", "10⁻¹⁵ m")
                    ),
                    L(
                        "Ядро ~10⁻¹⁵ м, ал атомның өзі ~10⁻¹⁰ м.",
                        "Ядро ~10⁻¹⁵ м, а сам атом ~10⁻¹⁰ м.",
                        "The nucleus is ~10⁻¹⁵ m, the atom itself ~10⁻¹⁰ m."
                    )
                )
            )
        ),
        Quiz(
            TopicIds.BOHR, QUIZ_XP, PASS_THRESHOLD, listOf(
                q(
                    "bo1",
                    L("Сутегі атомының негізгі күйдегі энергиясы қанша?", "Чему равна энергия атома водорода в основном состоянии?", "What is the ground-state energy of hydrogen?"),
                    0,
                    listOf(same("−13.6 эВ / eV"), same("0 эВ / eV"), same("+13.6 эВ / eV"), same("−3.4 эВ / eV")),
                    L("E₁ = −13.6/1² = −13.6 эВ.", "E₁ = −13.6/1² = −13.6 эВ.", "E₁ = −13.6/1² = −13.6 eV.")
                ),
                q(
                    "bo2",
                    L("Электрон жоғары орбитадан төменгі орбитаға өткенде не болады?", "Что происходит при переходе электрона на более низкую орбиту?", "What happens when an electron drops to a lower orbit?"),
                    1,
                    listOf(
                        L("Фотон жұтады", "Поглощает фотон", "It absorbs a photon"),
                        L("Фотон шығарады", "Испускает фотон", "It emits a photon"),
                        L("Ядроға құлайды", "Падает на ядро", "It falls into the nucleus"),
                        L("Энергия өзгермейді", "Энергия не меняется", "Energy does not change")
                    ),
                    L(
                        "Энергия айырмасы hν = Eₖ − Eₙ фотон түрінде бөлінеді.",
                        "Разность энергий hν = Eₖ − Eₙ уносится фотоном.",
                        "The energy difference hν = Eₖ − Eₙ leaves as a photon."
                    )
                ),
                q(
                    "bo3",
                    L("Бор бойынша n-ші орбитаның радиусы неге пропорционал?", "Чему пропорционален радиус n-й орбиты по Бору?", "In Bohr's model the radius of orbit n is proportional to…"),
                    2,
                    listOf(same("n"), same("1/n"), same("n²"), L("Тұрақты", "Постоянен", "Constant")),
                    L("rₙ = n² · r₁.", "rₙ = n² · r₁.", "rₙ = n² · r₁.")
                ),
                q(
                    "bo4",
                    L("n = 2 деңгейінің энергиясы қанша?", "Чему равна энергия уровня n = 2?", "What is the energy of level n = 2?"),
                    3,
                    listOf(same("−13.6 эВ / eV"), same("−6.8 эВ / eV"), same("−1.51 эВ / eV"), same("−3.4 эВ / eV")),
                    L("E₂ = −13.6/4 = −3.4 эВ.", "E₂ = −13.6/4 = −3.4 эВ.", "E₂ = −13.6/4 = −3.4 eV.")
                ),
                q(
                    "bo5",
                    L("Бор постулаттарын қай жылы ұсынды?", "В каком году Бор сформулировал свои постулаты?", "In which year did Bohr propose his postulates?"),
                    1,
                    listOf(same("1897"), same("1913"), same("1905"), same("1932")),
                    L("Бор моделі 1913 жылы жарияланды.", "Модель Бора опубликована в 1913 году.", "Bohr's model was published in 1913.")
                )
            )
        ),
        Quiz(
            TopicIds.RADIOACTIVITY, QUIZ_XP, PASS_THRESHOLD, listOf(
                q(
                    "ra1",
                    L("Альфа-бөлшек дегеніміз не?", "Что такое альфа-частица?", "What is an alpha particle?"),
                    0,
                    listOf(
                        L("Гелий ядросы", "Ядро гелия", "A helium nucleus"),
                        L("Электрон", "Электрон", "An electron"),
                        L("Фотон", "Фотон", "A photon"),
                        L("Нейтрон", "Нейтрон", "A neutron")
                    ),
                    L("α-бөлшек = ⁴₂He: 2 протон және 2 нейтрон.", "α-частица = ⁴₂He: 2 протона и 2 нейтрона.", "α particle = ⁴₂He: 2 protons and 2 neutrons.")
                ),
                q(
                    "ra2",
                    L("Қай сәуленің өтімділігі ең жоғары?", "У какого излучения наибольшая проникающая способность?", "Which radiation is the most penetrating?"),
                    2,
                    listOf(
                        L("Альфа", "Альфа", "Alpha"),
                        L("Бета", "Бета", "Beta"),
                        L("Гамма", "Гамма", "Gamma"),
                        L("Бәрі бірдей", "Все одинаковы", "All the same")
                    ),
                    L("Гамма-фотондардың заряды жоқ, сондықтан затпен әлсіз әрекеттеседі.", "Гамма-фотоны не заряжены и слабо взаимодействуют с веществом.", "Gamma photons are uncharged and interact weakly with matter.")
                ),
                q(
                    "ra3",
                    L("Жартылай ыдырау периоды дегеніміз не?", "Что такое период полураспада?", "What is the half-life?"),
                    1,
                    listOf(
                        L("Барлық ядро ыдырайтын уақыт", "Время распада всех ядер", "Time for all nuclei to decay"),
                        L("Ядролардың жартысы ыдырайтын уақыт", "Время распада половины ядер", "Time for half of the nuclei to decay"),
                        L("Бір ядроның өмір сүру уақыты", "Время жизни одного ядра", "Lifetime of a single nucleus"),
                        L("Сәулелену жиілігі", "Частота излучения", "Frequency of radiation")
                    ),
                    L("T уақыт өткенде ядролардың жартысы қалады: N = N₀/2.", "Через время T остаётся половина ядер: N = N₀/2.", "After time T half of the nuclei remain: N = N₀/2.")
                ),
                q(
                    "ra4",
                    L("β⁻-ыдырауда ядроның заряды қалай өзгереді?", "Как меняется заряд ядра при β⁻-распаде?", "How does the nuclear charge change in β⁻ decay?"),
                    3,
                    listOf(
                        L("2-ге кемиді", "Уменьшается на 2", "Decreases by 2"),
                        L("Өзгермейді", "Не меняется", "Does not change"),
                        L("1-ге кемиді", "Уменьшается на 1", "Decreases by 1"),
                        L("1-ге артады", "Увеличивается на 1", "Increases by 1")
                    ),
                    L("Нейтрон протонға айналады, сондықтан Z бірге артады.", "Нейтрон превращается в протон, поэтому Z растёт на единицу.", "A neutron turns into a proton, so Z grows by one.")
                ),
                q(
                    "ra5",
                    L("Радиоактивтілікті кім ашты?", "Кто открыл радиоактивность?", "Who discovered radioactivity?"),
                    0,
                    listOf(
                        L("Анри Беккерель", "Анри Беккерель", "Henri Becquerel"),
                        L("Эрнест Резерфорд", "Эрнест Резерфорд", "Ernest Rutherford"),
                        L("Нильс Бор", "Нильс Бор", "Niels Bohr"),
                        L("Альберт Эйнштейн", "Альберт Эйнштейн", "Albert Einstein")
                    ),
                    L("Беккерель 1896 жылы уран сәулеленуін ашты.", "Беккерель открыл излучение урана в 1896 году.", "Becquerel discovered uranium radiation in 1896.")
                )
            )
        ),
        Quiz(
            TopicIds.REACTIONS, QUIZ_XP, PASS_THRESHOLD, listOf(
                q(
                    "nr1",
                    L("Ядролық бөліну дегеніміз не?", "Что такое деление ядра?", "What is nuclear fission?"),
                    0,
                    listOf(
                        L("Ауыр ядроның екі жеңіл ядроға бөлінуі", "Распад тяжёлого ядра на два более лёгких", "A heavy nucleus splitting into two lighter ones"),
                        L("Жеңіл ядролардың қосылуы", "Слияние лёгких ядер", "Light nuclei merging"),
                        L("Электронның ұшып шығуы", "Вылет электрона", "Emission of an electron"),
                        L("Фотонның жұтылуы", "Поглощение фотона", "Absorption of a photon")
                    ),
                    L("Мысалы, ²³⁵U нейтронды жұтып, Ba мен Kr-ге бөлінеді.", "Например, ²³⁵U поглощает нейтрон и делится на Ba и Kr.", "For example, ²³⁵U absorbs a neutron and splits into Ba and Kr.")
                ),
                q(
                    "nr2",
                    L("Күннің ішінде қандай реакция жүреді?", "Какая реакция идёт в недрах Солнца?", "What reaction powers the Sun?"),
                    1,
                    listOf(
                        L("Ядролық бөліну", "Деление ядер", "Nuclear fission"),
                        L("Термоядролық синтез", "Термоядерный синтез", "Thermonuclear fusion"),
                        L("Альфа-ыдырау", "Альфа-распад", "Alpha decay"),
                        L("Химиялық жану", "Химическое горение", "Chemical burning")
                    ),
                    L("Күнде сутегі ядролары гелийге қосылады.", "В Солнце ядра водорода сливаются в гелий.", "In the Sun hydrogen nuclei fuse into helium.")
                ),
                q(
                    "nr3",
                    L("Масса мен энергияның байланысы қандай формуламен өрнектеледі?", "Какой формулой выражается связь массы и энергии?", "Which formula links mass and energy?"),
                    2,
                    listOf(same("E = mgh"), same("E = Fs"), same("E = Δm·c²"), same("E = mv²/2")),
                    L("Эйнштейн формуласы: E = Δm·c².", "Формула Эйнштейна: E = Δm·c².", "Einstein's formula: E = Δm·c².")
                ),
                q(
                    "nr4",
                    L("²³⁵U ядросының бөлінуін қандай бөлшек тудырады?", "Какая частица вызывает деление ядра ²³⁵U?", "Which particle triggers fission of ²³⁵U?"),
                    3,
                    listOf(
                        L("Протон", "Протон", "Proton"),
                        L("Электрон", "Электрон", "Electron"),
                        L("Альфа-бөлшек", "Альфа-частица", "Alpha particle"),
                        L("Нейтрон", "Нейтрон", "Neutron")
                    ),
                    L("Нейтронның заряды жоқ, сондықтан ядроға оңай жетеді.", "Нейтрон не заряжен и легко проникает в ядро.", "The neutron is uncharged and easily reaches the nucleus.")
                ),
                q(
                    "nr5",
                    L("Ядролық реакцияда не сақталады?", "Что сохраняется в ядерной реакции?", "What is conserved in a nuclear reaction?"),
                    0,
                    listOf(
                        L("Заряд пен массалық сан", "Заряд и массовое число", "Charge and mass number"),
                        L("Тек масса", "Только масса", "Only mass"),
                        L("Тек энергия", "Только энергия", "Only energy"),
                        L("Ештеңе", "Ничего", "Nothing")
                    ),
                    L("Реакцияның екі жағында Z мен A қосындылары тең.", "Суммы Z и A слева и справа от стрелки равны.", "The sums of Z and A are equal on both sides.")
                )
            )
        ),
        Quiz(
            TopicIds.REACTOR, QUIZ_XP, PASS_THRESHOLD, listOf(
                q(
                    "re1",
                    L("Баяулатқыштың міндеті қандай?", "Какова задача замедлителя?", "What does the moderator do?"),
                    0,
                    listOf(
                        L("Нейтрондарды баяулату", "Замедлять нейтроны", "Slows neutrons down"),
                        L("Отын беру", "Подавать топливо", "Supplies fuel"),
                        L("Электрон шығару", "Испускать электроны", "Emits electrons"),
                        L("Суды қайнату", "Кипятить воду", "Boils water")
                    ),
                    L("Баяу (жылулық) нейтрондар ²³⁵U ядросын тиімдірек бөледі.", "Медленные (тепловые) нейтроны эффективнее делят ядра ²³⁵U.", "Slow (thermal) neutrons split ²³⁵U more effectively.")
                ),
                q(
                    "re2",
                    L("Көбею коэффициенті k = 1 болса, не болады?", "Что означает коэффициент размножения k = 1?", "What does a multiplication factor k = 1 mean?"),
                    2,
                    listOf(
                        L("Қуат шексіз өседі", "Мощность неограниченно растёт", "Power grows without limit"),
                        L("Реакция сөнеді", "Реакция затухает", "The reaction dies out"),
                        L("Тұрақты режим", "Стационарный режим", "Steady state"),
                        L("Реактор тоқтайды", "Реактор останавливается", "The reactor stops")
                    ),
                    L("Әр буында нейтрон саны өзгермейді.", "Число нейтронов в каждом поколении не меняется.", "The neutron count stays the same each generation.")
                ),
                q(
                    "re3",
                    L("Басқарушы стерженьдер нені жұтады?", "Что поглощают управляющие стержни?", "What do control rods absorb?"),
                    1,
                    listOf(
                        L("Протондарды", "Протоны", "Protons"),
                        L("Нейтрондарды", "Нейтроны", "Neutrons"),
                        L("Электрондарды", "Электроны", "Electrons"),
                        L("Фотондарды", "Фотоны", "Photons")
                    ),
                    L("Бор мен кадмий нейтрондарды жақсы жұтады.", "Бор и кадмий хорошо поглощают нейтроны.", "Boron and cadmium absorb neutrons well.")
                ),
                q(
                    "re4",
                    L("Реакторда негізгі отын ретінде не қолданылады?", "Что служит основным топливом реактора?", "What is the main reactor fuel?"),
                    3,
                    listOf(same("⁴He"), same("¹²C"), same("¹H"), same("²³⁵U")),
                    L("Байытылған уранда ²³⁵U үлесі 3–5 %.", "В обогащённом уране доля ²³⁵U составляет 3–5 %.", "Enriched uranium contains 3–5 % of ²³⁵U.")
                ),
                q(
                    "re5",
                    L("БН-350 реакторы қай қалада жұмыс істеді?", "В каком городе работал реактор БН-350?", "In which city did the BN-350 reactor operate?"),
                    0,
                    listOf(
                        L("Ақтау", "Актау", "Aktau"),
                        L("Алматы", "Алматы", "Almaty"),
                        L("Шымкент", "Шымкент", "Shymkent"),
                        L("Қарағанды", "Караганда", "Karaganda")
                    ),
                    L("БН-350 Ақтауда 1973–1999 жылдары жұмыс істеді.", "БН-350 работал в Актау в 1973–1999 годах.", "BN-350 operated in Aktau from 1973 to 1999.")
                )
            )
        )
    )
}
