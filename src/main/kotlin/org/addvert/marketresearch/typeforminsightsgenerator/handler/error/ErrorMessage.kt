package org.addvert.marketresearch.typeforminsightsgenerator.handler.error

sealed class ErrorMessage
object UnableToParseForm : ErrorMessage()
object UnableToParseResponses : ErrorMessage()
object UnmatchedAnswerAttribute : ErrorMessage()