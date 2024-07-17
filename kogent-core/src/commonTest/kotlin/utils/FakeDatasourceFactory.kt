package utils

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource

object FakeDatasourceFactory {

    fun createSQLDatasource(): SQLDataSource =
        SQLDataSource(
            identifier = RandomPrimitivesFactory.genRandomString(),
            databaseType = SQLDataSource.DatabaseType.SQLITE,
            host = RandomPrimitivesFactory.genRandomString(),
            databaseName = RandomPrimitivesFactory.genRandomString(),
            username = RandomPrimitivesFactory.genRandomString(),
            password = RandomPrimitivesFactory.genRandomString(),
            query = RandomPrimitivesFactory.genRandomString(),
        )

    fun createAPIDatasource(): APIDataSource =
        APIDataSource(
            identifier = RandomPrimitivesFactory.genRandomString(),
            baseUrl = RandomPrimitivesFactory.genRandomString(),
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
}