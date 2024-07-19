package utils

import com.ralphdugue.kogent.indexing.domain.entities.Document
import utils.RandomPrimitivesFactory.genRandomFloatList
import utils.RandomPrimitivesFactory.genRandomString

object FakeDocumentFactory {
    fun createRandomSQLDocument(sourceName: String? = null): Document.SQLDocument =
        Document.SQLDocument(
            id = genRandomString(),
            sourceName = sourceName ?: genRandomString(),
            dialect = genRandomString(),
            schema = genRandomString(),
            query = genRandomString(),
            text = genRandomString(),
            embedding = genRandomFloatList(),
        )

    fun createRandomAPIDocument(sourceName: String? = null): Document.APIDocument =
        Document.APIDocument(
            id = genRandomString(),
            sourceName = sourceName ?: genRandomString(),
            text = genRandomString(),
            embedding = genRandomFloatList(),
        )

    fun createRandomDocumentList(size: Int = 10): List<Document> {
        val document = createRandomSQLDocument()
        val remaining = size / 2
        return List(size - remaining) {
            createRandomSQLDocument(document.sourceName)
        }.plus(List(remaining) {
            createRandomAPIDocument(document.sourceName)
        })
    }

    fun createRandomSQLDocumentList(size: Int = 10): List<Document.SQLDocument> =
        List(size) {
            createRandomSQLDocument()
        }

    fun createRandomAPIDocumentList(size: Int = 10): List<Document.APIDocument> =
        List(size) {
            createRandomAPIDocument()
        }
}
