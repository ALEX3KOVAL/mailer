package group.ost.mailer.domain.vo

import group.ost.mailer.domain.Checker.Companion.message
import group.ost.mailer.domain.failIf
import kotlinx.serialization.Serializable

/**
 * Адрес электронной почты
 */
@Serializable
class EmailAddress private constructor(val value: String) {
    init {
        failIf { value.isBlank() } message "Адрес электронной почты не может быть пустым"
        failIf { !value.matches("^[a-zA-Z0-9.!#\$%&\'*+=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$".toRegex()) } message "Недопустимые символы в e-mail адресе: $value"
    }

    override fun toString(): String = value

    companion object {
        operator fun invoke(value: String) = runCatching { EmailAddress(value.trim()) }
    }
}