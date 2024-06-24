package dataconnector.domain.entities

/**
 * This class represents the result of an SQL query.
 * @param columnNames The names of the columns in the result.
 * @param rows The rows in the result.
 * @param resultType The type of the result.
 */
data class KogentQueryResult(
    val columnNames: List<String>,
    val rows: List<Map<String, Any?>>,
    val resultType: ResultType = ResultType.SUCCESS,
) {
    /**
     * This enum represents the type of the result.
     */
    enum class ResultType {
        SUCCESS,
        FAILURE,
    }
}
