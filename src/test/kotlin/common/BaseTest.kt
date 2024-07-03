package common

import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
abstract class BaseTest {
    @OptIn(DelicateCoroutinesApi::class)
    protected val mainCoroutineDispatcher = newSingleThreadContext("main")
}
