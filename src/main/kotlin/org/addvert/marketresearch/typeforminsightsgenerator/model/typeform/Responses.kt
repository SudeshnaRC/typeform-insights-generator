package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform

import com.beust.klaxon.Json

data class Responses(
    @Json(name = "total_items")
    val totalItems: Int? = null,
    @Json(name = "page_count")
    val pageCount: Int? = null,
    @Json(name = "items")
    val items: List<Item>? = null
) {
    data class Item(
        @Json(name = "response_id")
        val responseId: String? = null,
        @Json(name = "answers")
        val answers: List<Answer>? = null
    ) {
        data class Answer(
            @Json(name = "field")
            val field: Field? = null,
            @Json(name = "type")
            val type: String? = null,
            @Json(name = "text")
            val text: String? = null,
            @Json(name = "choice")
            val choice: Choice? = null,
            @Json(name = "choices")
            val choices: Choices? = null,
            @Json(name = "email")
            val email: String? = null,
            @Json(name = "url")
            val url: String? = null,
            @Json(name = "file_url")
            val fileUrl: String? = null,
            @Json(name = "boolean")
            val boolean: Boolean? = null,
            @Json(name = "number")
            val number: Int? = null,
            @Json(name = "date")
            val date: String? = null,
            @Json(name = "payment")
            val payment: String? = null
        ) {
            data class Field(
                @Json(name = "id")
                val id: String? = null,
                @Json(name = "type")
                val type: String? = null,
                @Json(name = "ref")
                val ref: String? = null
            )
            data class Choice(
                @Json(name = "id")
                val id: String? = null,
                @Json(name = "ref")
                val ref: String? = null,
                @Json(name = "label")
                val label: String? = null
            )

            data class Choices(
                @Json(name = "ids")
                val ids: List<String>? = null,
                @Json(name = "refs")
                val refs: List<String>? = null,
                @Json(name = "labels")
                val labels: List<String>? = null
            )
        }
    }
}