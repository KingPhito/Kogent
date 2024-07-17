import com.ralphdugue.kogent.Kogent
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    Kogent.init {
        apiDataSource {
            identifier = "test"
            baseUrl = "http://localhost:8080"
        }
        huggingFaceEmbeddingConfig {

        }
        huggingFaceLLModelConfig {

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