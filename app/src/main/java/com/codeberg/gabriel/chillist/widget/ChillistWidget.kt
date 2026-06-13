package com.codeberg.gabriel.chillist.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.codeberg.gabriel.chillist.data.AppPreferencesRepository
import com.codeberg.gabriel.chillist.data.WidgetPreferences
import com.codeberg.gabriel.chillist.widget.LaunchAppAction.Companion.PackageNameKey

class ChillistWidget : GlanceAppWidget() {

    private var repository: AppPreferencesRepository? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = this.repository ?: AppPreferencesRepository(context.applicationContext).also { this.repository = it }

        provideContent {
            val prefs = repository.widgetPreferencesFlow.collectAsState(initial = WidgetPreferences()).value

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .cornerRadius(24.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (prefs.selectedApps.isEmpty()) {
                        Text(
                            text = "add apps in chillist settings",
                            modifier = GlanceModifier.clickable(actionStartActivity(actionStartActivityIntent(context))),
                            style = TextStyle(
                                color = androidx.glance.color.ColorProvider(Color.White),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        )
                    } else {
                        prefs.selectedApps.forEach { app ->
                            val label = app.customLabel ?: app.displayName
                            Text(
                                text = label,
                                modifier = GlanceModifier
                                    .padding(vertical = 6.dp)
                                    .clickable(
                                        actionRunCallback<LaunchAppAction>(
                                            actionParametersOf(PackageNameKey to app.packageName)
                                        )
                                    ),
                                style = TextStyle(
                                    color = androidx.glance.color.ColorProvider(Color.White),
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun actionStartActivityIntent(context: Context): android.content.Intent {
        return context.packageManager.getLaunchIntentForPackage(context.packageName) ?: android.content.Intent()
    }
}
