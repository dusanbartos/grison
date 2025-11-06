package sk.dusanbartos.grison.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sk.dusanbartos.grison.ui.theme.GrisonTheme
import sk.dusanbartos.grison.ui.theme.disabledContentAlpha
import sk.dusanbartos.grison.ui.theme.mediumContentAlpha

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String,
    action: (() -> Unit)? = null,
    actionLabel: String? = null,
) {
    EmptyState(
        modifier = modifier,
        painter = rememberVectorPainter(imageVector),
        title = title,
        subtitle = subtitle,
        action = action,
        actionLabel = actionLabel,
    )
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String,
    action: (() -> Unit)? = null,
    actionLabel: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(96.dp)
                .padding(bottom = 8.dp),
            painter = painter,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = disabledContentAlpha)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = subtitle,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = mediumContentAlpha)
        )

        if (action != null && actionLabel != null) {
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = { action() },
                elevation = null,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            ) {
                Text(actionLabel.uppercase())
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    GrisonTheme {
        Surface {
            Box {
                EmptyState(
                    imageVector = Icons.Rounded.WarningAmber,
                    title = "Empty State",
                    subtitle = "There is no item"
                )
            }
        }
    }
}