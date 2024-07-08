package group.ost.mailer.domain.dto

import group.ost.mailer.domain.vo.Attachment
import group.ost.mailer.domain.vo.EmailAddress
import group.ost.mailer.domain.vo.PlainText
import group.ost.mailer.domain.vo.Subject

/**
 * ДТО с данными для построения электронного письма
 *
 * @property fromAddress Адрес отправителя
 * @property toAddress Адрес получателя
 * @property plainText Текст тела письма
 * @property subject Тема письма
 * @property attachments Приложенные документы
 */
data class SendEmailDTO(
    val fromAddress: EmailAddress? = null,
    val toAddress: EmailAddress,
    val plainText: PlainText,
    val subject: Subject,
    val attachments: List<Attachment> = listOf()
)