package group.ost.mailer.domain

/**
 * Исключение "Не найдено"
 *
 * @param m Сообщение исключение
 */
internal class NotFoundException(m: String) : Exception(m)