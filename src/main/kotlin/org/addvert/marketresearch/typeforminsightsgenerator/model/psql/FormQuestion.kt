package org.addvert.marketresearch.typeforminsightsgenerator.model.psql

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class FormQuestion(
    @Column(name = "form_id")
    private val formId: String = "",
    @Column(name = "question_id")
    val questionId: String = ""
) : Serializable