package common

import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
abstract class BaseTest {
    @OptIn(DelicateCoroutinesApi::class)
    protected val mainCoroutineDispatcher = newSingleThreadContext("main")
}
