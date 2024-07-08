package group.ost.mailer.domain.service

import com.charleskorn.kaml.Yaml
import group.ost.mailer.domain.NotFoundException
import group.ost.mailer.domain.dto.SendEmailDTO
import group.ost.mailer.domain.ifNull
import group.ost.mailer.domain.vo.EmailAddress
import jakarta.mail.util.ByteArrayDataSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.simplejavamail.api.email.AttachmentResource
import org.simplejavamail.api.email.Email
import org.simplejavamail.api.email.EmailPopulatingBuilder
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl
import java.util.*

@Serializable
private data class AuthData(val username: String, val password: String)

/**
 * Сервис для отправки электронных писем
 */
class MailerService(emailingAuthProperties: String, properties: Properties) {
    /** Строитель электронных писем */
    private val emailBuilder: EmailPopulatingBuilder by lazy { EmailBuilder.startingBlank() }
    private val emailingAuthPropertiesMap by lazy {
        buildMap<String, AuthData> {
            Yaml.default
                .decodeFromString<Map<String, AuthData>>(emailingAuthProperties)
                .forEach { put(it.key, it.value) }
        }
    }
    private val defaultFromAddress by lazy {
        runCatching {
            ConfigLoader.getStringProperty(ConfigLoader.Property.DEFAULT_FROM_ADDRESS)!!
                .let { EmailAddress(it).getOrThrow() }
        }
    }
    private val defaultFromName by lazy {
        runCatching { ConfigLoader.getStringProperty(ConfigLoader.Property.DEFAULT_FROM_NAME)!! }
    }

    init {
        ConfigLoader.loadProperties(properties, false)
    }

    /**
     * Асинхронная отправка электронного письма
     *
     * @param dto ДТО с данными для построения электронного письма
     */
    fun sendEmailAsync(dto: SendEmailDTO): Result<Unit> = runCatching {
        dto.fromAddress?.value?.lowercase()
            ?.let { fromAddress ->
                MailerBuilder.withSMTPServerUsername(
                    emailingAuthPropertiesMap[fromAddress]
                        ?.username
                        ?: throw NotFoundException("В конфигурации mailer-сервиса не найден логин по почтовому адресу: $fromAddress")
                )
                    .withSMTPServerPassword(
                        emailingAuthPropertiesMap[fromAddress]
                            ?.password
                            ?: throw NotFoundException("В конфигурации mailer-сервиса не найден пароль по почтовому адресу: $fromAddress")
                    )
            }
            .ifNull { MailerRegularBuilderImpl() }
            .buildMailer()
            .sendMail(buildEmail(dto).getOrThrow(), true)
    }


    /**
     * Построение электронного письма
     *
     * @param dto ДТО с данными для построения электронного письма
     */
    private fun buildEmail(dto: SendEmailDTO): Result<Email> = runCatching {
        dto.attachments.map { att ->
            AttachmentResource(
                att.name.toString(),
                ByteArrayDataSource(att.content, att.mimeType.value)
                    .apply { name = att.name.toString() }
            )
        }
            .let { resources ->
                emailBuilder
                    .let { emailBuilder -> dto.fromAddress.ifNull { defaultFromAddress.getOrThrow() }.let { emailBuilder.from(defaultFromName.getOrThrow(), it.value) } }
                    .to(dto.toAddress.value)
                    .withSubject(dto.subject.value)
                    .withPlainText(dto.plainText.value)
                    .withAttachments(resources)
                    .buildEmailCompletedWithDefaultsAndOverrides()
            }
    }
}