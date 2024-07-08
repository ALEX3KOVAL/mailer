package group.ost.mailer.domain.vo

import group.ost.mailer.domain.Checker.Companion.message
import group.ost.mailer.domain.failIf
import kotlinx.serialization.Serializable

/**
 * Тема электронного письма
 */
@Serializable
class Subject private constructor(val value: String) {
    init {
        failIf { value.isBlank() } message "Тема письма не должна быть пустой"
    }

    companion object {
        operator fun invoke(value: String): Result<Subject> = runCatching { Subject(value.trim()) }
    }
}