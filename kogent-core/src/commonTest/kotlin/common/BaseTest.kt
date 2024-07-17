package common

import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
abstract class BaseTest {
    @OptIn(DelicateCoroutinesApi::class)
    protected val mainCoroutineDispatcher = newSingleThreadContext("main")


    @BeforeEach
    open fun setUp() {
        Dispatchers.setMain(mainCoroutineDispatcher)
    }

    @AfterEach
    open fun tearDown() {
        Dispatchers.resetMain()
    }
}
