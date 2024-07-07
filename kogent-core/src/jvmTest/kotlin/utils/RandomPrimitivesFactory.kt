package utils

/**
 * This object contains functions that generate random values.
 * These functions are useful for testing purposes.
 */
object RandomPrimitivesFactory {
    /**
     * This function generates a random string.
     * @param length The length of the string to generate.
     * @return The generated string.
     */
    fun genRandomString(length: Int = 12): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun genRandomInt(): Int = (0..Int.MAX_VALUE).random()

    fun genRandomFloat(): Float = (0..Float.MAX_VALUE.toInt()).random().toFloat()

    fun genRandomFloatArray(size: Int = 10): List<Float> = (1..size).map { genRandomFloat() }
}
