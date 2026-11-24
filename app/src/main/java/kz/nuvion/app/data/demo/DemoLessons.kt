package kz.nuvion.app.data.demo

import kz.nuvion.app.domain.model.AtomModelType
import kz.nuvion.app.domain.model.Lesson
import kz.nuvion.app.domain.model.LessonBlock.Formula
import kz.nuvion.app.domain.model.LessonBlock.Heading
import kz.nuvion.app.domain.model.LessonBlock.Illustration
import kz.nuvion.app.domain.model.LessonBlock.NumberedList
import kz.nuvion.app.domain.model.LessonBlock.Paragraph

object DemoLessons {

    private const val LESSON_XP = 50

    val all: List<Lesson> = listOf(
        Lesson(
            topicId = TopicIds.ATOM_MODELS,
            title = L("Атом модельдерінің дамуы", "Развитие моделей атома", "How atomic models evolved"),
            xpReward = LESSON_XP,
            modelType = AtomModelType.RUTHERFORD,
            blocks = listOf(
                Heading(L("Атом модельдерінің дамуы", "Развитие моделей атома", "How atomic models evolved")),
                Paragraph(
                    L(
                        "Атомның құрылысы туралы түсінік бір ғасыр ішінде түбегейлі өзгерді. Әр жаңа модель алдыңғысының кемшілігін түзеді:",
                        "Представление о строении атома за одно столетие изменилось коренным образом. Каждая новая модель исправляла недостатки предыдущей:",
                        "Our picture of the atom changed completely within a century. Each new model fixed the flaws of the previous one:"
                    )
                ),
                NumberedList(
                    listOf(
                        L(
                            "Томсон (1904): атом — оң зарядталған шар, электрондар оның ішінде «мейіздей» орналасқан.",
                            "Томсон (1904): атом — положительно заряженный шар, электроны вкраплены в него, как «изюм».",
                            "Thomson (1904): the atom is a positively charged sphere with electrons embedded like \"raisins\"."
                        ),
                        L(
                            "Резерфорд (1911): альфа-бөлшектердің шашырауы атомның массасы мен оң заряды кішкентай ядрода шоғырланғанын көрсетті.",
                            "Резерфорд (1911): рассеяние альфа-частиц показало, что масса и положительный заряд атома сосредоточены в крошечном ядре.",
                            "Rutherford (1911): alpha-particle scattering showed that the mass and positive charge sit in a tiny nucleus."
                        ),
                        L(
                            "Бор (1913): электрондар тек рұқсат етілген тұрақты орбиталарда қозғалады.",
                            "Бор (1913): электроны движутся только по разрешённым стационарным орбитам.",
                            "Bohr (1913): electrons move only along allowed stationary orbits."
                        )
                    )
                ),
                Formula(
                    L("r(ядро) ≈ 10⁻¹⁵ м   r(атом) ≈ 10⁻¹⁰ м", "r(ядро) ≈ 10⁻¹⁵ м   r(атом) ≈ 10⁻¹⁰ м", "r(nucleus) ≈ 10⁻¹⁵ m   r(atom) ≈ 10⁻¹⁰ m"),
                    L(
                        "Ядро атомнан шамамен 100 000 есе кіші",
                        "Ядро примерно в 100 000 раз меньше атома",
                        "The nucleus is about 100,000 times smaller than the atom"
                    )
                ),
                Illustration(
                    AtomModelType.RUTHERFORD,
                    L("Резерфордтың планетарлық моделі", "Планетарная модель Резерфорда", "Rutherford's planetary model")
                ),
                Heading(L("Резерфорд тәжірибесі", "Опыт Резерфорда", "Rutherford's experiment")),
                Paragraph(
                    L(
                        "Жұқа алтын фольгаға бағытталған альфа-бөлшектердің көпшілігі тура өтіп кетті, бірақ шамамен 8000 бөлшектің біреуі 90°-тан үлкен бұрышқа ауытқыды. Бұл атомның ортасында өте тығыз әрі шағын ядро бар екенін дәлелдеді.",
                        "Большинство альфа-частиц, направленных на тонкую золотую фольгу, проходили насквозь, но примерно одна из 8000 отклонялась более чем на 90°. Это доказало, что в центре атома находится очень плотное и малое ядро.",
                        "Most alpha particles fired at thin gold foil passed straight through, but about one in 8,000 bounced back by more than 90°. This proved there is a tiny, very dense nucleus at the centre of the atom."
                    )
                )
            )
        ),
        Lesson(
            topicId = TopicIds.BOHR,
            title = L("Бор атомының постулаттары", "Постулаты атома Бора", "Bohr's postulates"),
            xpReward = LESSON_XP,
            modelType = AtomModelType.BOHR,
            blocks = listOf(
                Heading(L("Бор атомының постулаттары", "Постулаты атома Бора", "Bohr's postulates")),
                Paragraph(
                    L(
                        "Нильс Бор 1913 жылы атомның жаңа моделін ұсынды. Негізгі постулаттар:",
                        "Нильс Бор в 1913 году предложил новую модель атома. Основные постулаты:",
                        "In 1913 Niels Bohr proposed a new model of the atom. Its main postulates:"
                    )
                ),
                NumberedList(
                    listOf(
                        L(
                            "Электрондар ядроның айналасында тұрақты орбиталарда қозғалады.",
                            "Электроны движутся вокруг ядра по стационарным орбитам.",
                            "Electrons move around the nucleus along stationary orbits."
                        ),
                        L(
                            "Орбиталардың энергиясы квантталған: E = -13.6/n² эВ.",
                            "Энергия орбит квантована: E = -13.6/n² эВ.",
                            "Orbit energies are quantised: E = -13.6/n² eV."
                        ),
                        L(
                            "Электрон бір орбитадан екіншісіне секіргенде фотон шығарады немесе жұтады.",
                            "При переходе с одной орбиты на другую электрон испускает или поглощает фотон.",
                            "When an electron jumps between orbits it emits or absorbs a photon."
                        )
                    )
                ),
                Formula(L("Eₙ = −13.6 / n²  (эВ)", "Eₙ = −13.6 / n²  (эВ)", "Eₙ = −13.6 / n²  (eV)"), L("Сутегі атомындағы деңгей энергиясы", "Энергия уровня в атоме водорода", "Energy level of hydrogen")),
                Formula("hν = Eₖ − Eₙ", L("Шығарылған фотонның энергиясы", "Энергия испущенного фотона", "Energy of the emitted photon")),
                Formula("rₙ = n² · r₁,   r₁ = 0.53 Å", L("Орбита радиусы", "Радиус орбиты", "Orbit radius")),
                Illustration(AtomModelType.BOHR, L("Бор атомы: квантталған орбиталар", "Атом Бора: квантованные орбиты", "Bohr atom: quantised orbits")),
                Heading(L("Сутегі спектрі", "Спектр водорода", "Hydrogen spectrum")),
                Paragraph(
                    L(
                        "Электрон жоғары деңгейлерден n = 2 деңгейіне ауысқанда көзге көрінетін Бальмер сериясы пайда болады. Мысалы, 3 → 2 ауысуы қызыл түсті 656 нм сызығын береді.",
                        "При переходах электрона с верхних уровней на уровень n = 2 возникает видимая серия Бальмера. Например, переход 3 → 2 даёт красную линию 656 нм.",
                        "Transitions from higher levels down to n = 2 produce the visible Balmer series. For example, 3 → 2 gives the red 656 nm line."
                    )
                )
            )
        ),
        Lesson(
            topicId = TopicIds.RADIOACTIVITY,
            title = L("Радиоактивті ыдырау", "Радиоактивный распад", "Radioactive decay"),
            xpReward = LESSON_XP,
            modelType = AtomModelType.NUCLEUS,
            blocks = listOf(
                Heading(L("Радиоактивті ыдырау", "Радиоактивный распад", "Radioactive decay")),
                Paragraph(
                    L(
                        "1896 жылы Анри Беккерель уран тұздарының көзге көрінбейтін сәуле шығаратынын ашты. Тұрақсыз ядролар өздігінен ыдырап, сәулелердің үш түрін шығарады:",
                        "В 1896 году Анри Беккерель обнаружил, что соли урана испускают невидимое излучение. Нестабильные ядра самопроизвольно распадаются и испускают три вида излучения:",
                        "In 1896 Henri Becquerel found that uranium salts emit invisible radiation. Unstable nuclei decay spontaneously and emit three kinds of radiation:"
                    )
                ),
                NumberedList(
                    listOf(
                        L(
                            "α-сәуле — гелий ядролары (⁴₂He). Қағаз парағы тоқтатады.",
                            "α-излучение — ядра гелия (⁴₂He). Задерживается листом бумаги.",
                            "α radiation — helium nuclei (⁴₂He). Stopped by a sheet of paper."
                        ),
                        L(
                            "β-сәуле — электрондар немесе позитрондар. Алюминий пластинасы тоқтатады.",
                            "β-излучение — электроны или позитроны. Задерживается алюминиевой пластиной.",
                            "β radiation — electrons or positrons. Stopped by an aluminium plate."
                        ),
                        L(
                            "γ-сәуле — жоғары энергиялы фотондар. Қалың қорғасын қабаты әлсіретеді.",
                            "γ-излучение — фотоны высокой энергии. Ослабляется толстым слоем свинца.",
                            "γ radiation — high-energy photons. Weakened by a thick layer of lead."
                        )
                    )
                ),
                Formula(
                    "N = N₀ · 2^(−t/T)",
                    L(
                        "Радиоактивті ыдырау заңы, T — жартылай ыдырау периоды",
                        "Закон радиоактивного распада, T — период полураспада",
                        "Decay law, T is the half-life"
                    )
                ),
                Formula("²³⁸₉₂U → ²³⁴₉₀Th + ⁴₂He", L("Альфа-ыдырау мысалы", "Пример альфа-распада", "Example of alpha decay")),
                Illustration(AtomModelType.NUCLEUS, L("Тұрақсыз ядро", "Нестабильное ядро", "An unstable nucleus"))
            )
        ),
        Lesson(
            topicId = TopicIds.REACTIONS,
            title = L("Ядролық реакциялар", "Ядерные реакции", "Nuclear reactions"),
            xpReward = LESSON_XP,
            modelType = AtomModelType.FISSION,
            blocks = listOf(
                Heading(L("Ядролық реакциялар", "Ядерные реакции", "Nuclear reactions")),
                Paragraph(
                    L(
                        "Ядролық реакция — ядролардың бір-бірімен немесе бөлшектермен әрекеттесу нәтижесінде түрленуі. Реакцияда электр заряды мен массалық сан сақталады.",
                        "Ядерная реакция — превращение ядер при взаимодействии друг с другом или с частицами. В реакции сохраняются электрический заряд и массовое число.",
                        "A nuclear reaction transforms nuclei when they interact with each other or with particles. Electric charge and mass number are conserved."
                    )
                ),
                NumberedList(
                    listOf(
                        L(
                            "Бөліну: ауыр ядро (²³⁵U) нейтронды жұтып, екі жеңіл ядроға бөлінеді.",
                            "Деление: тяжёлое ядро (²³⁵U) поглощает нейтрон и делится на два более лёгких ядра.",
                            "Fission: a heavy nucleus (²³⁵U) absorbs a neutron and splits into two lighter nuclei."
                        ),
                        L(
                            "Синтез: жеңіл ядролар (сутегі изотоптары) қосылып, ауыррақ ядро түзеді.",
                            "Синтез: лёгкие ядра (изотопы водорода) сливаются в более тяжёлое ядро.",
                            "Fusion: light nuclei (hydrogen isotopes) merge into a heavier nucleus."
                        ),
                        L(
                            "Екі жағдайда да масса ақауы энергияға айналады.",
                            "В обоих случаях дефект массы превращается в энергию.",
                            "In both cases the mass defect turns into energy."
                        )
                    )
                ),
                Formula("E = Δm · c²", L("Эйнштейн формуласы", "Формула Эйнштейна", "Einstein's formula")),
                Formula("²³⁵U + n → ¹⁴¹Ba + ⁹²Kr + 3n", L("Уранның бөлінуі", "Деление урана", "Fission of uranium")),
                Illustration(AtomModelType.FISSION, L("Ядроның бөлінуі", "Деление ядра", "Nuclear fission")),
                Paragraph(
                    L(
                        "Бір ²³⁵U ядросы бөлінгенде шамамен 200 МэВ энергия бөлінеді — бұл бір химиялық реакциядан миллиондаған есе көп.",
                        "При делении одного ядра ²³⁵U выделяется около 200 МэВ — в миллионы раз больше, чем в одной химической реакции.",
                        "Splitting one ²³⁵U nucleus releases about 200 MeV — millions of times more than a single chemical reaction."
                    )
                )
            )
        ),
        Lesson(
            topicId = TopicIds.REACTOR,
            title = L("Ядролық реактор", "Ядерный реактор", "The nuclear reactor"),
            xpReward = LESSON_XP,
            modelType = AtomModelType.REACTOR,
            blocks = listOf(
                Heading(L("Ядролық реактор қалай жұмыс істейді?", "Как работает ядерный реактор?", "How does a nuclear reactor work?")),
                Paragraph(
                    L(
                        "Реакторда басқарылатын тізбекті бөліну реакциясы жүреді. Әр бөліну 2–3 жаңа нейтрон береді, олар келесі ядроларды бөледі. Реактордың негізгі бөліктері:",
                        "В реакторе идёт управляемая цепная реакция деления. Каждое деление даёт 2–3 новых нейтрона, которые делят следующие ядра. Основные части реактора:",
                        "A reactor runs a controlled fission chain reaction. Each fission releases 2–3 neutrons that split further nuclei. Main parts of a reactor:"
                    )
                ),
                NumberedList(
                    listOf(
                        L("Отын — байытылған уран (²³⁵U).", "Топливо — обогащённый уран (²³⁵U).", "Fuel — enriched uranium (²³⁵U)."),
                        L(
                            "Баяулатқыш — су немесе графит нейтрондарды баяулатады.",
                            "Замедлитель — вода или графит замедляют нейтроны.",
                            "Moderator — water or graphite slows neutrons down."
                        ),
                        L(
                            "Басқарушы стерженьдер — бор немесе кадмий артық нейтрондарды жұтады.",
                            "Управляющие стержни — бор или кадмий поглощают лишние нейтроны.",
                            "Control rods — boron or cadmium absorb excess neutrons."
                        ),
                        L(
                            "Жылу тасымалдағыш энергияны турбинаға жеткізеді.",
                            "Теплоноситель передаёт энергию на турбину.",
                            "The coolant carries the energy to the turbine."
                        )
                    )
                ),
                Formula(
                    "k = Nᵢ₊₁ / Nᵢ",
                    L(
                        "k = 1 — тұрақты режим, k > 1 — қуат өседі, k < 1 — реакция сөнеді",
                        "k = 1 — стационарный режим, k > 1 — мощность растёт, k < 1 — реакция затухает",
                        "k = 1 — steady state, k > 1 — power grows, k < 1 — the reaction dies out"
                    )
                ),
                Illustration(AtomModelType.REACTOR, L("Тізбекті реакция", "Цепная реакция", "Chain reaction")),
                Paragraph(
                    L(
                        "Қазақстанда, Ақтау қаласында 1973–1999 жылдары БН-350 жылдам нейтрондар реакторы жұмыс істеді. Ол электр қуатын беріп, теңіз суын тұщытты.",
                        "В Казахстане, в Актау, в 1973–1999 годах работал реактор на быстрых нейтронах БН-350. Он вырабатывал электроэнергию и опреснял морскую воду.",
                        "In Aktau, Kazakhstan, the BN-350 fast-neutron reactor operated from 1973 to 1999, producing electricity and desalinating sea water."
                    )
                )
            )
        )
    )
}
