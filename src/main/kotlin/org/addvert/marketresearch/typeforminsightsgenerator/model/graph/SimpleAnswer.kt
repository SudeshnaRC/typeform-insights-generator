package org.addvert.marketresearch.typeforminsightsgenerator.model.graph

import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses

data class SimpleAnswer(
    val type: String,
    val choice: Responses.Item.Answer.Choice
)