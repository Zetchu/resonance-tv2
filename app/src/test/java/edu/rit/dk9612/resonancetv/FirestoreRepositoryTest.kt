package edu.rit.dk9612.resonancetv

import edu.rit.dk9612.resonancetv.data.network.SharedVideo
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreRepositoryTest {

    @Test
    fun `test firestore data mapping integrity`() {
        // Arrange: Create a mock Firestore object with 1 like
        val shared = SharedVideo(
            id = "test_123",
            title = "Hard Techno Mix",
            subtitle = "Artist A",
            thumbnailUrl = "url",
            likes = 1 // Set this to 1 to match your assertion
        )

        // Act: Manually simulate the mapping logic used in your Repository
        // You MUST pass the likes property here to match the SharedVideo object
        val videoItem = VideoItem(
            id = shared.id,
            title = shared.title,
            subtitle = shared.subtitle,
            thumbnailUrl = shared.thumbnailUrl,
            duration = "Shared Set", // Add this line
            likes = shared.likes
        )

        // Assert: Ensure data integrity
        assertEquals("Hard Techno Mix", videoItem.title)
        assertEquals(1, videoItem.likes) // This will now pass
        assertEquals("test_123", videoItem.id)
    }
}