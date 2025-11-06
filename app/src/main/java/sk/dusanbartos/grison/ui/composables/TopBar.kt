package sk.dusanbartos.grison.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sk.dusanbartos.grison.ui.theme.GrisonTheme
import sk.dusanbartos.grison.ui.theme.highContentAlpha
import sk.dusanbartos.grison.ui.theme.mediumContentAlpha

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    contentPadding: PaddingValues = PaddingValues(bottom = 16.dp),
    content: @Composable () -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxWidth(), color = background) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun SmallTopBarRow(
    onBack: () -> Unit,
    title: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "back"
            )
        }

        title()

        Spacer(modifier = Modifier.weight(1f))

        actions()
    }
}

@Composable
fun TopBarRow(
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "back"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        actions()
    }
}

@Composable
fun TopBarTitle(
    text: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(start = 16.dp),
    style: TextStyle = MaterialTheme.typography.titleLarge
        .copy(fontWeight = FontWeight.ExtraBold)
) {
    Text(
        modifier = modifier.padding(paddingValues),
        text = text,
        style = style,
        color = LocalContentColor.current.copy(alpha = highContentAlpha)
    )
}

@Composable
fun SmallTopBarTitle(
    text: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(start = 8.dp),
) {
    TopBarTitle(
        text = text,
        modifier = modifier,
        paddingValues = paddingValues,
        style = MaterialTheme.typography.titleLarge
            .copy(fontWeight = FontWeight.ExtraBold)
    )
}

@Composable
fun TopBarSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(start = 16.dp),
) {
    Text(
        modifier = modifier.padding(paddingValues),
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = LocalContentColor.current.copy(alpha = mediumContentAlpha)
    )
}

@Composable
fun TopBarCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(start = 16.dp),
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = LocalContentColor.current.copy(alpha = mediumContentAlpha)
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    GrisonTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(bottom = 24.dp)) {
                TopBar {
                    TopBarRow(onBack = {})
                    TopBarTitle("Screen Title")
                    TopBarSubtitle("Screen Subtitle")
                    TopBarCaption("Some small caption below")
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SmallPreview() {
    GrisonTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(bottom = 24.dp)) {
                TopBar {
                    SmallTopBarRow(
                        onBack = {},
                        title = {
                            SmallTopBarTitle("Screen Title")
                        },
                    )
                    TopBarSubtitle("Screen Subtitle")
                    TopBarCaption("Some small caption below")
                }
            }
        }
    }
}