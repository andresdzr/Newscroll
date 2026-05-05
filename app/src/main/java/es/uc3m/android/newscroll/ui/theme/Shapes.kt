package es.uc3m.android.newscroll.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(

    // 🔹 ELEMENTOS PEQUEÑOS (chips, inputs pequeños)
    small = RoundedCornerShape(10.dp),

    // 🔹 COMPONENTES PRINCIPALES (cards, chat items, botones)
    medium = RoundedCornerShape(14.dp),

    // 🔹 MODALES / SHEETS / CONTAINERS GRANDES
    large = RoundedCornerShape(18.dp)
)