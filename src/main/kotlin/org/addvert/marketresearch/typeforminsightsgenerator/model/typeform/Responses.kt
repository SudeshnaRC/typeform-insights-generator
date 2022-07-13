package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform

import io.micronaut.core.annotation.Introspected
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Introspected
@Serializable
data class Responses(
    @SerialName("total_items") val totalItems: Int?,
    @SerialName("page_count") val pageCount: Int?,
    @SerialName("items") val items: List<Item>?
) {
    @Introspected
    @Serializable
    data class Item(
        @SerialName("response_id") val responseId: String,
        @SerialName("answers") val answers: List<Answer>?
    ) {
        @Introspected
        @Serializable
        data class Answer(
            @SerialName("field") val field: Field,
            @SerialName("type") val type: String,
            @SerialName("text") val text: String?,
            @SerialName("choice") val choice: Choice?,
            @SerialName("choices") val choices: List<Choice>?,
            @SerialName("email") val email: String?,
            @SerialName("url") val url: String?,
            @SerialName("file_url") val fileUrl: String?,
            @SerialName("boolean") val boolean: Boolean?,
            @SerialName("number") val number: Int?,
            @SerialName("date") val date: String?,
            @SerialName("payment") val payment: String?
        ) {
            @Introspected
            @Serializable
            data class Field(
                @SerialName("id") val id: String,
                @SerialName("type") val type: String,
                @SerialName("ref") val ref: String
            )
            @Introspected
            @Serializable
            data class Choice(
                @SerialName("id") val id: String,
                @SerialName("ref") val ref: String,
                @SerialName("label") val label: String
            )
        }
    }
}