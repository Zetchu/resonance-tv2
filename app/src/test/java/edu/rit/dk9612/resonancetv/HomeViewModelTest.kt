package edu.rit.dk9612.resonancetv

import edu.rit.dk9612.resonancetv.data.model.VideoCategory
import edu.rit.dk9612.resonancetv.data.repository.YouTubeRepository
import edu.rit.dk9612.resonancetv.ui.HomeUiState
import edu.rit.dk9612.resonancetv.ui.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // We replace the Main dispatcher so coroutines run synchronously in tests
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- 1. A FAKE REPOSITORY TO CONTROL THE DATA ---
    // We override the real repository so we don't actually hit the internet
    class FakeYouTubeRepository(private val shouldReturnError: Boolean = false) : YouTubeRepository() {
        override suspend fun getHomeCategories(): List<VideoCategory> {
            if (shouldReturnError) {
                // Simulate an empty list (which triggers an error in our ViewModel)
                return emptyList()
            }
            // Simulate a successful API call
            return listOf(
                VideoCategory("Test Category", emptyList())
            )
        }
    }

    // --- 2. THE TESTS ---

    @Test
    fun `when viewmodel is initialized, initial state is Loading`() = runTest {
        val fakeRepo = FakeYouTubeRepository()
        val viewModel = HomeViewModel(repository = fakeRepo)

        // The very first state before coroutines finish should be Loading
        val currentState = viewModel.uiState.value
        assertTrue("State should be Loading", currentState is HomeUiState.Loading)
    }

    @Test
    fun `when fetchHomeContent succeeds, state becomes Success with categories`() = runTest {
        // Given a repo that returns data
        val fakeRepo = FakeYouTubeRepository(shouldReturnError = false)
        val viewModel = HomeViewModel(repository = fakeRepo)

        // When we let the coroutines execute
        advanceUntilIdle()

        // Then the state should update to Success
        val currentState = viewModel.uiState.value
        assertTrue("State should be Success", currentState is HomeUiState.Success)

        // And the data should match our fake repo
        val successState = currentState as HomeUiState.Success
        assertEquals("Test Category", successState.categories[0].categoryName)
    }

    @Test
    fun `when fetchHomeContent gets empty list, state becomes Error`() = runTest {
        // Given a repo that simulates a failure/empty response
        val fakeRepo = FakeYouTubeRepository(shouldReturnError = true)
        val viewModel = HomeViewModel(repository = fakeRepo)

        // When we let the coroutines execute
        advanceUntilIdle()

        // Then the state should update to Error
        val currentState = viewModel.uiState.value
        assertTrue("State should be Error", currentState is HomeUiState.Error)
    }
}