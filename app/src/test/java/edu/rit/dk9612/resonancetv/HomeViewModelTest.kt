package edu.rit.dk9612.resonancetv

import edu.rit.dk9612.resonancetv.data.model.VideoCategory
import edu.rit.dk9612.resonancetv.data.repository.YouTubeRepository
import edu.rit.dk9612.resonancetv.ui.viewmodels.HomeUiState
import edu.rit.dk9612.resonancetv.ui.viewmodels.HomeViewModel
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

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    class FakeYouTubeRepository(private val shouldReturnError: Boolean = false) : YouTubeRepository() {
        override suspend fun getHomeCategories(): List<VideoCategory> {
            if (shouldReturnError) {

                return emptyList()
            }
            return listOf(
                VideoCategory("Test Category", emptyList())
            )
        }
    }


    @Test
    fun `when viewmodel is initialized, initial state is Loading`() = runTest {
        val fakeRepo = FakeYouTubeRepository()
        val viewModel = HomeViewModel(repository = fakeRepo)
        val currentState = viewModel.uiState.value
        assertTrue("State should be Loading", currentState is HomeUiState.Loading)
    }

    @Test
    fun `when fetchHomeContent succeeds, state becomes Success with categories`() = runTest {
        val fakeRepo = FakeYouTubeRepository(shouldReturnError = false)
        val viewModel = HomeViewModel(repository = fakeRepo)
        advanceUntilIdle()
        val currentState = viewModel.uiState.value
        assertTrue("State should be Success", currentState is HomeUiState.Success)

        val successState = currentState as HomeUiState.Success
        assertEquals("Test Category", successState.categories[0].categoryName)
    }

    @Test
    fun `when fetchHomeContent gets empty list, state becomes Error`() = runTest {
        val fakeRepo = FakeYouTubeRepository(shouldReturnError = true)
        val viewModel = HomeViewModel(repository = fakeRepo)

        advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertTrue("State should be Error", currentState is HomeUiState.Error)
    }
}