package com.codeberg.gabriel.chillist.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.codeberg.gabriel.chillist.data.AppPreferencesRepository
import com.codeberg.gabriel.chillist.data.WidgetPreferences
import com.codeberg.gabriel.chillist.widget.LaunchAppAction.Companion.PackageNameKey

import android.widget.RemoteViews
import com.codeberg.gabriel.chillist.R
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.glance.Image
import androidx.glance.ImageProvider

class ChillistWidget : GlanceAppWidget() {

    private var repository: AppPreferencesRepository? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = this.repository ?: AppPreferencesRepository(context.applicationContext).also { this.repository = it }

        provideContent {
            val prefs = repository.widgetPreferencesFlow.collectAsState(initial = WidgetPreferences()).value

            val horizontalAlignment = when (prefs.alignment) {
                "center" -> Alignment.CenterHorizontally
                "right" -> Alignment.End
                else -> Alignment.Start
            }

            // Parse text color
            val textColorProvider = if (prefs.useDynamicColors) {
                GlanceTheme.colors.onSurface
            } else {
                try {
                    val colorHex = if (prefs.textColorHex.startsWith("#")) prefs.textColorHex else "#${prefs.textColorHex}"
                    val color = Color(android.graphics.Color.parseColor(colorHex))
                    androidx.glance.color.ColorProvider(day = color, night = color)
                } catch (e: Exception) {
                    androidx.glance.color.ColorProvider(day = Color.DarkGray, night = Color.LightGray)
                }
            }

            // Parse background color with custom opacity
            val backgroundColorProvider = try {
                val opacityHex = String.format("%02X", (prefs.backgroundOpacity * 255).toInt())
                var baseColorHex = if (prefs.backgroundColorHex.startsWith("#")) prefs.backgroundColorHex.substring(1) else prefs.backgroundColorHex
                // If it was already AARRGGBB, take only RRGGBB
                if (baseColorHex.length == 8) baseColorHex = baseColorHex.substring(2)

                val color = Color(android.graphics.Color.parseColor("#$opacityHex$baseColorHex"))
                androidx.glance.color.ColorProvider(day = color, night = color)
            } catch (e: Exception) {
                androidx.glance.color.ColorProvider(day = Color.Transparent, night = Color.Transparent)
            }

            val fontFamily = when (prefs.fontFamily) {
                "serif" -> FontFamily.Serif
                "monospace" -> FontFamily.Monospace
                "cursive" -> FontFamily.Cursive
                "nothing" -> FontFamily("ndot") // Use the name from res/font/ndot.xml
                else -> FontFamily.SansSerif
            }

            val dividerColor = if (prefs.useDynamicColors) {
                GlanceTheme.colors.outline
            } else {
                try {
                    val dayDiv = Color(android.graphics.Color.parseColor("#20000000"))
                    val nightDiv = Color(android.graphics.Color.parseColor("#20FFFFFF"))
                    androidx.glance.color.ColorProvider(day = dayDiv, night = nightDiv)
                } catch (ignored: Exception) {
                    androidx.glance.color.ColorProvider(day = Color.Gray, night = Color.Gray)
                }
            }

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(backgroundColorProvider)
                    .cornerRadius(24.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (prefs.showDateTime) {
                        if (prefs.fontFamily == "nothing") {
                            val sdf = java.text.SimpleDateFormat("EEEE, MMM d", java.util.Locale.getDefault())
                            val dateText = sdf.format(java.util.Date())
                            val colorInt = try {
                                val colorHex = if (prefs.textColorHex.startsWith("#")) prefs.textColorHex else "#${prefs.textColorHex}"
                                android.graphics.Color.parseColor(colorHex)
                            } catch (e: Exception) {
                                android.graphics.Color.WHITE
                            }
                            val bitmap = textToBitmap(
                                context = context,
                                text = dateText,
                                fontSizeSp = (prefs.fontSizeSp - 4).toFloat(),
                                textColor = colorInt
                            )
                            Image(
                                provider = ImageProvider(bitmap),
                                contentDescription = dateText,
                                modifier = GlanceModifier.padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                            )
                        } else {
                            val layoutRes = R.layout.widget_text_clock
                            val remoteViews = RemoteViews(context.packageName, layoutRes).apply {
                                val colorInt = try {
                                    val colorHex = if (prefs.textColorHex.startsWith("#")) prefs.textColorHex else "#${prefs.textColorHex}"
                                    android.graphics.Color.parseColor(colorHex)
                                } catch (e: Exception) {
                                    android.graphics.Color.WHITE
                                }
                                setTextColor(R.id.widget_text_clock, colorInt)
                                setTextViewTextSize(R.id.widget_text_clock, android.util.TypedValue.COMPLEX_UNIT_SP, (prefs.fontSizeSp - 4).toFloat())
                            }
                            AndroidRemoteViews(remoteViews)
                        }
                    }

                    if (prefs.showSearch) {
                        if (prefs.fontFamily == "nothing") {
                            val colorInt = try {
                                val colorHex = if (prefs.textColorHex.startsWith("#")) prefs.textColorHex else "#${prefs.textColorHex}"
                                android.graphics.Color.parseColor(colorHex)
                            } catch (e: Exception) {
                                android.graphics.Color.WHITE
                            }
                            val bitmap = textToBitmap(
                                context = context,
                                text = "search.",
                                fontSizeSp = prefs.fontSizeSp.toFloat(),
                                textColor = colorInt
                            )
                            Image(
                                provider = ImageProvider(bitmap),
                                contentDescription = "search.",
                                modifier = GlanceModifier
                                    .padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                                    .clickable(actionRunCallback<LaunchSearchAction>())
                            )
                        } else {
                            Text(
                                text = "search.",
                                modifier = GlanceModifier
                                    .padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                                    .clickable(actionRunCallback<LaunchSearchAction>()),
                                style = TextStyle(
                                    color = textColorProvider,
                                    fontSize = prefs.fontSizeSp.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        
                        if (prefs.showDividers && (prefs.showApps && prefs.selectedApps.isNotEmpty())) {
                            Box(
                                modifier = GlanceModifier
                                    .width(60.dp)
                                    .height(1.dp)
                                    .background(dividerColor)
                            ) {}
                        }
                    }

                    if (prefs.showApps) {
                        if (prefs.selectedApps.isEmpty()) {
                            Text(
                                text = "add apps in chillist settings",
                                modifier = GlanceModifier.clickable(actionStartActivity(actionStartActivityIntent(context))),
                                style = TextStyle(
                                    color = textColorProvider,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            )
                        } else {
                            prefs.selectedApps.forEachIndexed { index, app ->
                                val label = app.customLabel ?: app.displayName
                                val displayTitle = when (prefs.textCase) {
                                    "lowercase" -> label.lowercase()
                                    "uppercase" -> label.uppercase()
                                    else -> label
                                }

                                if (prefs.fontFamily == "nothing") {
                                    val colorInt = try {
                                        val colorHex = if (prefs.textColorHex.startsWith("#")) prefs.textColorHex else "#${prefs.textColorHex}"
                                        android.graphics.Color.parseColor(colorHex)
                                    } catch (e: Exception) {
                                        android.graphics.Color.WHITE
                                    }
                                    val bitmap = textToBitmap(
                                        context = context,
                                        text = displayTitle,
                                        fontSizeSp = prefs.fontSizeSp.toFloat(),
                                        textColor = colorInt
                                    )
                                    Image(
                                        provider = ImageProvider(bitmap),
                                        contentDescription = displayTitle,
                                        modifier = GlanceModifier
                                            .padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                                            .clickable(
                                                actionRunCallback<LaunchAppAction>(
                                                    actionParametersOf(PackageNameKey to app.packageName)
                                                )
                                            )
                                    )
                                } else {
                                    Text(
                                        text = displayTitle,
                                        modifier = GlanceModifier
                                            .padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                                            .clickable(
                                                actionRunCallback<LaunchAppAction>(
                                                    actionParametersOf(PackageNameKey to app.packageName)
                                                )
                                            ),
                                        style = TextStyle(
                                            color = textColorProvider,
                                            fontSize = prefs.fontSizeSp.sp,
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }

                                // Optional divider line
                                if (prefs.showDividers && (index < prefs.selectedApps.lastIndex)) {
                                    Box(
                                        modifier = GlanceModifier
                                            .width(60.dp)
                                            .height(1.dp)
                                            .background(dividerColor)
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun textToBitmap(
        context: Context,
        text: String,
        fontSizeSp: Float,
        textColor: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_SP,
                fontSizeSp,
                context.resources.displayMetrics
            )
            color = textColor
            try {
                typeface = ResourcesCompat.getFont(context, R.font.ndot47)
            } catch (e: Exception) {
                typeface = android.graphics.Typeface.MONOSPACE
            }
            textAlign = Paint.Align.LEFT
        }

        val fm = paint.fontMetricsInt
        val height = (fm.bottom - fm.top).coerceAtLeast(1)
        val baseline = -fm.top

        val width = (paint.measureText(text) + 0.5f).toInt().coerceAtLeast(1) + 4
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 2f, baseline.toFloat(), paint)
        return bitmap
    }

    // Helper to get intent to launch setup screen
    private fun actionStartActivityIntent(context: Context): android.content.Intent {
        return context.packageManager.getLaunchIntentForPackage(context.packageName) ?: android.content.Intent()
    }
}
