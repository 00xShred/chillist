package com.codeberg.gabriel.chillist.widget

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class LaunchSearchAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Use standard Android Global Search intent
        val intent = Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Some launchers also respond to "query" being empty to just open the UI
            putExtra(SearchManager.QUERY, "")
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: Try a generic search action if global search isn't available
            val fallbackIntent = Intent(Intent.ACTION_SEARCH).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                // If everything fails, do nothing or log
            }
        }
    }
}
