package com.guanyc.stock.discipline.presentation.settings

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun SettingsBasicEmailButtonItem(
    @StringRes
    title: Int,
    subtitle: String = "",
    @DrawableRes
    icon: Int,
    link: String = "yc.guan@gmail.com",
    onClick: () -> Unit = {}
){
    val context = LocalContext.current
    SettingsItemCard(
        cornerRadius = 12.dp,
        hPadding = 8.dp,
        vPadding = 8.dp,
        onClick = {
            if (link.isNotBlank()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(link)) // recipient email address
                    putExtra(Intent.EXTRA_SUBJECT, "Request a feature/Report a bug on Born Win") // email subject
                    putExtra(Intent.EXTRA_TEXT, "Here is the email contents.") // email body content
                }

                intent.setType("message/rfc822")

                context.startActivity(intent)
            } else onClick()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title),
                modifier = Modifier.size(24.dp, 24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.body2,
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.body2,
        )
    }
}


@Composable
fun SettingsBasicLinkItem(
    @StringRes
    title: Int,
    subtitle: String = "",
    imageVector: ImageVector,
    link: String = "",
    onClick: () -> Unit = {}){
    val context = LocalContext.current
    SettingsItemCard(
        cornerRadius = 12.dp,
        hPadding = 8.dp,
        vPadding = 8.dp,
        onClick = {
            if (link.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                context.startActivity(intent)
            } else onClick()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(id = title),
                modifier = Modifier.size(24.dp, 24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.body2,
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
fun SettingsBasicLinkItem(
    @StringRes
    title: Int,
    subtitle: String = "",
    @DrawableRes
    icon: Int,
    link: String = "",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    SettingsItemCard(
        cornerRadius = 12.dp,
        hPadding = 8.dp,
        vPadding = 8.dp,
        onClick = {
            if (link.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                context.startActivity(intent)
            } else onClick()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title),
                modifier = Modifier.size(24.dp, 24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.body2,
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.body2,
        )
    }
}