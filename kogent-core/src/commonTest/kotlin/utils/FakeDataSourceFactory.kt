package utils

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceType
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource

object FakeDataSourceFactory {

    fun createSQLDatasource(
        identifier: String = RandomPrimitivesFactory.genRandomString(),
        databaseName: String = RandomPrimitivesFactory.genRandomString(),
    ): SQLDataSource =
        SQLDataSource(
            identifier = identifier,
            databaseType = SQLDataSource.DatabaseType.SQLITE,
            host = RandomPrimitivesFactory.genRandomString(),
            databaseName = databaseName,
            username = RandomPrimitivesFactory.genRandomString(),
            password = RandomPrimitivesFactory.genRandomString(),
            query = RandomPrimitivesFactory.genRandomString(),
        )

    fun createAPIDatasource(
        identifier: String = RandomPrimitivesFactory.genRandomString(),
        baseUrl: String = RandomPrimitivesFactory.genRandomString(),
    ): APIDataSource =
        APIDataSource(
            identifier = identifier,
            baseUrl = baseUrl,
            endpoint = RandomPrimitivesFactory.genRandomString(),
            method = APIDataSource.HttpMethod.POST,
            headers = mapOf(
                RandomPrimitivesFactory.genRandomString() to RandomPrimitivesFactory.genRandomString(),
                RandomPrimitivesFactory.genRandomString() to RandomPrimitivesFactory.genRandomString(),
            ),
            queryParams = mapOf(
                RandomPrimitivesFactory.genRandomString() to RandomPrimitivesFactory.genRandomString(),
                RandomPrimitivesFactory.genRandomString() to RandomPrimitivesFactory.genRandomString(),
            ),
            body = RandomPrimitivesFactory.genRandomString(),
        )

    fun createDataSourceCollection(
        identifiers: List<String> = List(2) { RandomPrimitivesFactory.genRandomString() },
        dataSourceType: DataSourceType = DataSourceType.SQL,
    ): List<DataSource> {
        return identifiers.map {
            val dataSourceName = RandomPrimitivesFactory.genRandomString()
            when (dataSourceType) {
                DataSourceType.SQL -> createSQLDatasource(identifier = it, databaseName = dataSourceName)
                DataSourceType.API -> createAPIDatasource(identifier = it, baseUrl = dataSourceName)
            }
        }
    }

    fun createSQLDatasourceCollection(
        identifiers: List<String> = List(3) { RandomPrimitivesFactory.genRandomString() },
    ): List<SQLDataSource> {
        return identifiers.map {
            val dataSourceName = RandomPrimitivesFactory.genRandomString()
            createSQLDatasource(identifier = it, databaseName = dataSourceName)
        }
    }
}