package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform


data class Form(
    val id: String,
    val title: String,
    val fields: List<Field>
) {
    data class Field(
        val id: String,
        val title: String,
        val ref: String,
        val type: String
    )
}