package group.ost.mailer.domain

/**
 * Чекер заданного условия, если условие выполняется, то подниамется исключение модуля
 */
internal class Checker(private val condition: () -> Boolean) {
    private fun check(message: String) {
        if (condition()) { throw RuntimeException(message)
        }
    }

    companion object {
        infix fun Checker.message(message: String) {
            this.check(message)
        }
    }
}

/**
 * Поднять исключение модуля, если выполняется условие
 *
 * @param condition Замыкание с условием
 *
 * @throws EmailingException
 */
internal fun failIf(condition: () -> Boolean): Checker = Checker(condition)

fun <T> T?.ifNull(action: () -> T): T {
    if (this == null) return action()
    return this
}