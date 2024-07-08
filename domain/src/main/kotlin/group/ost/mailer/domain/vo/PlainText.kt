package group.ost.mailer.domain.vo

import kotlinx.serialization.Serializable

/**
 * Текст тела сообщения
 */
@Serializable
data class PlainText(val value: String)