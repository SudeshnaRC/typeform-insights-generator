package org.addvert.marketresearch.typeforminsightsgenerator.model.psql


import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "form_questions")
data class FormEntity(
    @EmbeddedId
    private var formQuestion: FormQuestion = FormQuestion(),
    @Column(name = "question_title")
    val questionTitle: String = ""
)