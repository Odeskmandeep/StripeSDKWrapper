package singhsarae.badshah.jetpackcomposepractice.customColorsForDayNightTheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import singhsarae.badshah.jetpackcomposepractice.utilities.Constants

data class CustomColorsPalette(
    val dynamicTextColor: Color = Color.Unspecified,
    val dynamicTextColorReverse: Color = Color.Unspecified,
    val dynamicSurfaceColor: Color = Color.Unspecified,
    val dynamicSurfaceColorReverse: Color = Color.Unspecified,
)
val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val OnLightCustomColorsPalette = CustomColorsPalette(
    dynamicTextColor = Color(Constants.TEXT_BLACK),
    dynamicTextColorReverse = Color(Constants.TEXT_WHITE),
    dynamicSurfaceColor = Color(Constants.LIGHT_WHITE),
    dynamicSurfaceColorReverse = Color(Constants.LIGHT_BLACK),
)

val OnDarkCustomColorsPalette = CustomColorsPalette(
    dynamicTextColor = Color(Constants.TEXT_WHITE),
    dynamicTextColorReverse = Color(Constants.TEXT_BLACK),
    dynamicSurfaceColor = Color(Constants.LIGHT_BLACK),
    dynamicSurfaceColorReverse = Color(Constants.LIGHT_WHITE),
)