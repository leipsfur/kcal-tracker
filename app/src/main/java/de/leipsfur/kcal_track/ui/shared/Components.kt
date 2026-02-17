package de.leipsfur.kcal_track.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KcalTrackCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
    )
    val cardModifier = modifier.fillMaxWidth()

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            elevation = elevation,
            colors = colors,
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            elevation = elevation,
            colors = colors,
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                content = content
            )
        }
    }
}

@Composable
fun KcalTrackHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}
