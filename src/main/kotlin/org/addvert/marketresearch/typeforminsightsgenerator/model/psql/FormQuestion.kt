package org.addvert.marketresearch.typeforminsightsgenerator.model.psql

import org.hibernate.annotations.Immutable
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

@Embeddable
@Immutable
data class PartialForm(
    @Column(name = "form_id", insertable = false, updatable = false)
    private var formId: String = ""
)