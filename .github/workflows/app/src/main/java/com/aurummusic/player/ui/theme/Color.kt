package com.aurummusic.player.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BlackPrimary = Color(0xFF0A0A0E)
val BlackDeep = Color(0xFF050508)
val SurfaceDark = Color(0xFF100B1A)
val SurfaceCard = Color(0xFF171126)
val SurfaceCardHover = Color(0xFF22173B)
val SurfaceGlow = Color(0xFF28184C)

val PurpleAccent = Color(0xFF8B5CF6)
val PurpleGlow = Color(0xFFA855F7)
val PurpleDeep = Color(0xFF6D28D9)
val VioletNeon = Color(0xFFC084FC)
val GoldAccent = Color(0xFFFACC15)
val RoseAccent = Color(0xFFF43F5E)

val BorderSubtle = Color(0xFF332057)
val BorderHighlight = Color(0xFF5B309E)

val TextWhite = Color(0xFFF9FAFB)
val TextLight = Color(0xFFE5E7EB)
val TextMuted = Color(0xFF9CA3AF)
val TextDark = Color(0xFF6B7280)

val AurumGlowGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2A154D),
        Color(0xFF130A24),
        Color(0xFF0A0A0E)
    )
)

val AurumCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1C1333),
        Color(0xFF120B21)
    )
)

val AurumPurpleButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF8B5CF6),
        Color(0xFFA855F7)
    )
)
