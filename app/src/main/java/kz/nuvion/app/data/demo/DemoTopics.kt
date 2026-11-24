package kz.nuvion.app.data.demo

import kz.nuvion.app.domain.model.LocalizedText
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.TopicIcon

/** Short helper for three-language demo content: Kazakh, Russian, English. */
internal fun L(kk: String, ru: String, en: String) = LocalizedText(kk, ru, en)

internal fun same(text: String) = LocalizedText.same(text)

object TopicIds {
    const val ATOM_MODELS = "atom_models"
    const val BOHR = "bohr_postulates"
    const val RADIOACTIVITY = "radioactivity"
    const val REACTIONS = "nuclear_reactions"
    const val REACTOR = "reactor_physics"
}

object DemoTopics {
    val all: List<Topic> = listOf(
        Topic(
            id = TopicIds.ATOM_MODELS,
            order = 0,
            title = L("Атом модельдері", "Модели атома", "Atomic Models"),
            subtitle = L("Томсон, Резерфорд, Бор", "Томсон, Резерфорд, Бор", "Thomson, Rutherford, Bohr"),
            icon = TopicIcon.ATOM
        ),
        Topic(
            id = TopicIds.BOHR,
            order = 1,
            title = L("Бор постулаттары", "Постулаты Бора", "Bohr Theory"),
            subtitle = L("Кванттық секірулер", "Квантовые скачки", "Quantum jumps"),
            icon = TopicIcon.WAVES
        ),
        Topic(
            id = TopicIds.RADIOACTIVITY,
            order = 2,
            title = L("Радиоактивтілік", "Радиоактивность", "Radioactivity"),
            subtitle = L("Альфа, бета, гамма", "Альфа, бета, гамма", "Alpha, beta, gamma"),
            icon = TopicIcon.RADIATION
        ),
        Topic(
            id = TopicIds.REACTIONS,
            order = 3,
            title = L("Ядролық реакциялар", "Ядерные реакции", "Nuclear Reactions"),
            subtitle = L("Бөліну және синтез", "Деление и синтез", "Fission and fusion"),
            icon = TopicIcon.REACTION
        ),
        Topic(
            id = TopicIds.REACTOR,
            order = 4,
            title = L("Реактор физикасы", "Физика реактора", "Reactor Physics"),
            subtitle = L("Тізбекті реакция", "Цепная реакция", "Chain reaction"),
            icon = TopicIcon.REACTOR
        )
    )
}
