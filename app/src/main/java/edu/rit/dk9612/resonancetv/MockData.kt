package edu.rit.dk9612.resonancetv

data class VideoItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val duration: String,
    val thumbnailUrl: String = "",
    val likes: Int = 0,
    val description: String = ""
)

data class VideoCategory(
    val categoryName: String,
    val videos: List<VideoItem>
)

object MockData {
    val homeCategories = listOf(
        VideoCategory(
            "Hard Bounce & Techno Mixes",
            listOf(
                VideoItem("1", "Industrial Peak - Vault Mix", "Borgore • 2.4M views", "1:45:00"),
                VideoItem("2", "Cybernetic Rhythms", "Neon Ghost • 890K views", "0:58:22"),
                VideoItem("3", "Nocturnal System", "Frequency 7 • 1.2M views", "2:12:45"),
                VideoItem("4", "Acid House Revival", "DJ Setup • 500K views", "1:15:00")
            )
        ),
        VideoCategory(
            "Recent Boiler Rooms",
            listOf(
                VideoItem("5", "London Underground Live", "Static Flux • 3.1M views", "1:30:10"),
                VideoItem("6", "Berlin Warehouse Project", "Xenon Pulse • 1.8M views", "2:00:00"),
                VideoItem("7", "Tokyo Afterhours", "LYRA-7 • 950K views", "1:10:20"),
                VideoItem("8", "Detroit Techno Showcase", "Void Runner • 2.2M views", "1:45:30")
            )
        )
    )
}