package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform

import com.beust.klaxon.Json

data class Responses(
    @Json(name = "total_items")
    val totalItems: Int,
    @Json(name = "page_count")
    val pageCount: Int,
    @Json(name = "items")
    val items: List<Item>
) {
    data class Item(
        @Json(name = "response_id")
        val responseId: String,
        @Json(name = "answers")
        val answers: List<Answer>
    ) {
        data class Answer(
            @Json(name = "field")
            val field: Field,
            @Json(name = "type")
            val type: String,
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
                @Json(name = "ref")
                val ref: String
            )
            data class Choice(
                @Json(name = "label")
                val label: String
            )

            data class Choices(
                @Json(name = "ids")
                val ids: List<String>,
                @Json(name = "refs")
                val refs: List<String>,
                @Json(name = "labels")
                val labels: List<String>
            )
        }
    }
}