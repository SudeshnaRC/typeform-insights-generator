package org.addvert.marketresearch.typeforminsightsgenerator.model.psql
import javax.persistence.*

@Entity
@Table(name = "form_questions")
data class FormEntity(
    @EmbeddedId
    var formQuestion: FormQuestion = FormQuestion(),
    @Embedded
    var form: PartialForm = PartialForm(),
    @Column(name = "question_title")
    val questionTitle: String = "",
    @Column(name = "question_type")
    val questionType: String = ""

)