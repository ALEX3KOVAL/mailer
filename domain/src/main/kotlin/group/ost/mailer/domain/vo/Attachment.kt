package group.ost.mailer.domain.vo

import group.ost.mailer.domain.Checker.Companion.message
import group.ost.mailer.domain.failIf
import java.net.URLConnection
import kotlinx.serialization.Serializable

/**
 * Вложение в электронное письмо
 *
 * @property name Имя вложения
 * @property content Содержимое вложения
 * @property mimeType Медиа-тип
 */
@Serializable
class Attachment private constructor(val name: Name, val content: ByteArray, val mimeType: MimeType) {
    /**
     * Медиа-тип вложения письма
     */
    @Serializable
    class MimeType private constructor(val value: String) {
        override fun toString(): String = value

        companion object {
            operator fun invoke(value: String) = runCatching {
                val mimeType = URLConnection.guessContentTypeFromName(value.trim().lowercase())

                failIf { mimeType.isNullOrBlank() } message "Не найден медиа-тип для вложения $value"

                MimeType(mimeType)
            }
        }
    }

    /**
     * Имя вложения
     *
     * @property title Имя файла без формата
     * @property format Формат вложения
     */
    @Serializable
    class Name private constructor(val title: Title, val format: Format) {
        @Serializable
        class Title private constructor(val value: String) {
            init { failIf { value.isBlank() } message "Имя файла не может быть пустым" }

            companion object {
                operator fun invoke(value: String) = runCatching { Title(value.trim()) }
            }
        }

        override fun toString(): String = "${title.value}.${format.value}"

        @Serializable
        class Format private constructor(val value: String) {
            init { failIf { value.isBlank() } message "Формат вложения не может быть пустым" }

            companion object {
                operator fun invoke(value: String) = runCatching { Format(value.trim().lowercase()) }
            }
        }

        companion object {
            operator fun invoke(attachmentName: String): Result<Name> = runCatching {
                failIf { attachmentName.isBlank() } message "Имя вложения не может быть пустым"
                failIf { !Regex("\\..+").containsMatchIn(attachmentName) } message "Отсутствует формат вложения"

                val splittedName = attachmentName.trim().split(".")

                val fileNamePair = Pair(
                    splittedName.dropLast(1).joinToString("."),
                    splittedName.last().lowercase()
                )

                Name(
                    title = Title(fileNamePair.first).getOrThrow(),
                    format = Format(fileNamePair.second).getOrThrow()
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

    companion object {
        operator fun invoke(name: String, content: ByteArray) = runCatching {
            Attachment(
                name = Name(name).getOrThrow(),
                content = content,
                mimeType = MimeType(name).getOrThrow()
            )
        }
    }
}