package es.uc3m.android.newscroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun ChatItem(
    username: String,
    photoUrl: String,
    onClick: () -> Unit
) {

    // Custom interaction to remove default ripple effect
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium) // improves touch feedback
            .clickable(
                interactionSource = interactionSource,
                indication = null, // removes default ripple
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Avatar image (fallback if empty)
        AsyncImage(
            model = if (photoUrl.isNotEmpty()) photoUrl else "https://via.placeholder.com/150",
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Username and optional secondary info
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = username,
                style = MaterialTheme.typography.bodyLarge
            )

            // Placeholder for future message preview
            // Text("Last message...", style = MaterialTheme.typography.bodySmall)
        }

        // Small visual indicator (e.g., online or active)
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        )
    }
}