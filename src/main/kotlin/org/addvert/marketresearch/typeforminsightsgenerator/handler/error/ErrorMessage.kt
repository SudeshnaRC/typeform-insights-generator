package org.addvert.marketresearch.typeforminsightsgenerator.handler.error

sealed class ErrorMessage
object UnableToParseForm : ErrorMessage()
object FormNotFound : ErrorMessage()
object UnableToParseResponses : ErrorMessage()
object ResponsesNotFound : ErrorMessage()
object UnmatchedAnswerAttribute : ErrorMessage()