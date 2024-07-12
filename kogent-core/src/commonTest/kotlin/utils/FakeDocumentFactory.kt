package utils

import com.ralphdugue.kogent.indexing.domain.entities.Document
import utils.RandomPrimitivesFactory.genRandomFloatList
import utils.RandomPrimitivesFactory.genRandomString

object FakeDocumentFactory {
    fun genRandomSQLDocument(sourceName: String? = null): Document.SQLDocument =
        Document.SQLDocument(
            id = genRandomString(),
            sourceName = sourceName ?: genRandomString(),
            dialect = genRandomString(),
            schema = genRandomString(),
            embedding = genRandomFloatList(),
        )

    fun genRandomAPIDocument(sourceName: String? = null): Document.APIDocument =
        Document.APIDocument(
            id = genRandomString(),
            sourceName = sourceName ?: genRandomString(),
            embedding = genRandomFloatList(),
        )

    fun genRandomDocumentList(size: Int = 10): List<Document.SQLDocument> {
        val document = genRandomSQLDocument()
        return List(size) {
            genRandomSQLDocument(document.sourceName)
        }
    }
}
