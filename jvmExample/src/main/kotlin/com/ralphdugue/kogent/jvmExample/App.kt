import com.ralphdugue.kogent.Kogent
import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val properties = File("local.properties").inputStream().use {
        System.getProperties().apply {
            load(it)
        }
    }
    val ballDontLieKey = properties.getProperty("ballDontLieKey")
    val huggingFaceKey = properties.getProperty("huggingFaceKey")
    Kogent.init {
        apiDataSource {
            identifier = "activePlayers"
            baseUrl = "https://api.balldontlie.io/v1"
            endpoint = "/players/active"
            method = APIDataSource.HttpMethod.GET
            headers = mapOf("Authorization" to ballDontLieKey)
        }
        huggingFaceEmbeddingConfig {
            apiToken = huggingFaceKey
        }
        huggingFaceLLModelConfig {
            apiToken = huggingFaceKey
        }
        vectorIndexConfig {
            connectionString = "localhost:19530"
            vectorDatabaseType = VectorStoreOptions.MILVUS
        }

    }
    val queryEngine = Kogent.getQueryEngine()
    println("Enter a query:")
    val line = readlnOrNull()
    GlobalScope.launch {
        while (line != null) {
            println(queryEngine.processQuery(line))
        }
    }
}