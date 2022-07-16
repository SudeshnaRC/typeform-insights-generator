package org.addvert.marketresearch.typeforminsightsgenerator.model.psql

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class FormQuestion(
    @Column(name = "form_id")
    private var formId: String = "",
    @Column(name = "question_id")
    private var questionId: String = ""
) : Serializable