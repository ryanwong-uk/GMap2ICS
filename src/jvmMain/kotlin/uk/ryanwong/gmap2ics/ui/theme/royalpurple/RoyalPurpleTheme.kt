/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.ui.theme.royalpurple

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RoyalPurpleTheme(
    isSystemInDarkTheme: Boolean = false, // Compose library broken. isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {

    val colors = if (isSystemInDarkTheme) {
        Colors(
            primary = md_theme_dark_primary,
            onPrimary = md_theme_dark_onPrimary,
            primaryVariant = md_theme_dark_primaryContainer,
            secondary = md_theme_dark_tertiary,
            onSecondary = md_theme_dark_onTertiary,
            secondaryVariant = md_theme_light_onTertiaryContainer,
            background = md_theme_dark_background,
            onBackground = md_theme_dark_onBackground,
            surface = md_theme_dark_surface,
            onSurface = md_theme_dark_onSurface,
            error = md_theme_dark_error,
            onError = md_theme_dark_onError,
            isLight = false,
        )
    } else {
        Colors(
            primary = md_theme_light_primary,
            onPrimary = md_theme_light_onPrimary,
            primaryVariant = md_theme_light_primaryContainer,
            secondary = md_theme_light_tertiary,
            onSecondary = md_theme_light_onTertiary,
            secondaryVariant = md_theme_light_tertiaryContainer,
            background = md_theme_light_background,
            onBackground = md_theme_light_onBackground,
            surface = md_theme_light_surface,
            onSurface = md_theme_light_onSurface,
            error = md_theme_light_error,
            onError = md_theme_light_onError,
            isLight = true,
        )
    }

    MaterialTheme(
        colors = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
    )
}
