package com.ralphdugue.kogent.data.adapters.registry

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.domain.entities.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class LocalDataSourceRegistry(private val database: DataSourceRegistryDB) : DataSourceRegistry {
    override suspend fun getDataSources(): Result<List<DataSource>> {
        val json = Json { ignoreUnknownKeys = true }
        return runCatching {
            database.dataSourceRegistryQueries.selectAll()
                .executeAsList()
                .map { json.decodeFromString<DataSource>(it.content) }
        }
    }

    override suspend fun registerDataSource(dataSource: DataSource): Result<Unit> {
        val json = Json { ignoreUnknownKeys = true }
        return runCatching {
            database.dataSourceRegistryQueries.insert(
                identifier = dataSource.identifier,
                sourceType = dataSource.dataSourceType.toString(),
                content = json.encodeToString(dataSource)
            )
        }
    }

    override suspend fun getDataSourceById(identifier: String): Result<DataSource> {
        val json = Json { ignoreUnknownKeys = true }
        return runCatching {
            database.dataSourceRegistryQueries.selectById(identifier)
                .executeAsOneOrNull()
                ?.let {
                    when (it.sourceType) {
                        DataSourceType.API.toString() -> json.decodeFromString<APIDataSource>(it.content)
                        DataSourceType.SQL.toString() -> json.decodeFromString<SQLDataSource>(it.content)
                        else -> throw IllegalArgumentException("Unknown data source type")
                    }
                }!!
        }
    }

    override suspend fun removeDataSource(identifier: String): Result<Unit> {
        return runCatching { database.dataSourceRegistryQueries.delete(identifier) }
    }

    override suspend fun updateDataSource(dataSource: DataSource): Result<Unit> {
        val json = Json { ignoreUnknownKeys = true }
        return runCatching {
            database.dataSourceRegistryQueries.update(
                identifier = dataSource.identifier,
                content = json.encodeToString(dataSource)
            )
        }
    }
}

