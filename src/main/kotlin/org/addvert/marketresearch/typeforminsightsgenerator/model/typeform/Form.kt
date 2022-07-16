package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform


data class Form(
    val id: String? = null,
    val title: String? = null,
    val fields: List<Field>? = null
) {
    data class Field(
        val id: String? = null,
        val title: String? = null,
        val ref: String? = null,
        val properties: Properties? = null,
        val type: String? = null
    ) {
        data class Properties(
            val choices: List<Choice>? = null
        ) {
            data class Choice(
                val id: String? = null,
                val ref: String? = null,
                val label: String? = null
            )
        }
    }
}