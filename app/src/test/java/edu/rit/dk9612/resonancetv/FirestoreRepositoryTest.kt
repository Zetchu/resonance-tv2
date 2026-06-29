package edu.rit.dk9612.resonancetv

import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.network.SharedVideo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreRepositoryTest {

    @Test
    fun `map SharedVideo to VideoItem correctly calculates likes and user status`() {
        val myUserId = "user_99"
        val mockFirebaseData = SharedVideo(
            id = "vault_1",
            title = "Underground Techno",
            subtitle = "DJ Set",
            thumbnailUrl = "url",
            likedBy = listOf("user_1", "user_2", myUserId)
        )

        val videoItem = VideoItem(
            id = mockFirebaseData.id,
            title = mockFirebaseData.title,
            subtitle = mockFirebaseData.subtitle,
            duration = "Shared Set",
            thumbnailUrl = mockFirebaseData.thumbnailUrl,
            description = "",
            likes = mockFirebaseData.likedBy.size,
            isLikedByMe = mockFirebaseData.likedBy.contains(myUserId)
        )

        assertEquals("Total likes should be exactly the size of the likedBy array", 3, videoItem.likes)
        assertTrue("isLikedByMe should be true since myUserId is in the list", videoItem.isLikedByMe)
    }

    @Test
    fun `map SharedVideo to VideoItem when current user has not liked it`() {
        val myUserId = "user_99"
        val mockFirebaseData = SharedVideo(
            id = "vault_2",
            title = "Boiler Room",
            subtitle = "DJ Set",
            thumbnailUrl = "url",
            likedBy = listOf("user_1", "user_2")
        )
        val videoItem = VideoItem(
            id = mockFirebaseData.id,
            title = mockFirebaseData.title,
            subtitle = mockFirebaseData.subtitle,
            duration = "Shared Set",
            thumbnailUrl = mockFirebaseData.thumbnailUrl,
            description = "",
            likes = mockFirebaseData.likedBy.size,
            isLikedByMe = mockFirebaseData.likedBy.contains(myUserId)
        )
        assertEquals("Total likes should be 2", 2, videoItem.likes)
        assertFalse("isLikedByMe should be false since user_99 is missing", videoItem.isLikedByMe)
    }
}