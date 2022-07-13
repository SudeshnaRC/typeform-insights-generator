package org.addvert.marketresearch.typeforminsightsgenerator.model.typeform

import io.micronaut.core.annotation.Introspected
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Introspected
@Serializable
data class Form(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("fields") val fields: List<Field>

){
    @Introspected
    @Serializable
    data class Field(
        @SerialName("id") val id: String,
        @SerialName("title") val title: String,
        @SerialName("ref") val ref: String,
        @SerialName("properties") val properties: Properties,
        @SerialName("type") val type: String
    ){
        @Introspected
        @Serializable
        data class Properties(
            @SerialName("choices") val choices: List<Choice>?,
            @SerialName("description") val description: String?
        ){
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