package utils

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.indexing.domain.entities.Document
import utils.RandomPrimitivesFactory.genRandomFloatList

object FakeDocumentFactory {
    fun createSQLDocument(
        dataSource: SQLDataSource = FakeDataSourceFactory.createSQLDatasource()
    ): Document.SQLDocument =
        Document.SQLDocument(
            id = dataSource.identifier,
            sourceName = dataSource.databaseName,
            dialect = dataSource.databaseType.name,
            schema = dataSource.databaseName,
            query = dataSource.query,
            text = dataSource.query,
            embedding = genRandomFloatList(),
        )

    fun createAPIDocument(
        dataSource: APIDataSource = FakeDataSourceFactory.createAPIDatasource()
    ): Document.APIDocument =
        Document.APIDocument(
            id = dataSource.identifier,
            sourceName = dataSource.baseUrl,
            baseUrl = dataSource.baseUrl,
            endpoint = dataSource.endpoint,
            text = dataSource.body ?: "",
            embedding = genRandomFloatList(),
        )

    fun createDocumentCollection(
        dataSourceList: List<DataSource> = FakeDataSourceFactory.createDataSourceCollection()
    ): List<Document> {
        return dataSourceList.map {
            when (it) {
                is SQLDataSource -> createSQLDocument(it)
                is APIDataSource -> createAPIDocument(it)
                else -> throw IllegalArgumentException("Unknown DataSource type")
            }
        }
    }


}
