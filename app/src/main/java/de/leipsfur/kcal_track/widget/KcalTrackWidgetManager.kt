package de.leipsfur.kcal_track.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.*
import de.leipsfur.kcal_track.KcalTrackApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

object KcalTrackWidgetManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateWidget(context: Context) {
        scope.launch {
            val app = context.applicationContext as KcalTrackApplication
            val db = app.database
            val today = LocalDate.now()

            // Fetch data
            val foodKcalFlow = db.foodEntryDao().getTotalKcalForDate(today)
            val activityKcalFlow = db.activityEntryDao().getTotalKcalForDate(today)
            val userSettingsFlow = db.userSettingsDao().get()

            val foodKcal = try { foodKcalFlow.first() } catch (e: Exception) { 0 }
            val activityKcal = try { activityKcalFlow.first() } catch (e: Exception) { 0 }
            val settings = userSettingsFlow.firstOrNull()

            val bmr = settings?.bmr ?: 0
            val tdee = bmr + activityKcal
            val remaining = tdee - foodKcal

            // Update widget state
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(KcalTrackWidget::class.java)

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, definition = PreferencesGlanceStateDefinition, glanceId = glanceId) { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[KcalTrackWidget.remainingKcalKey] = remaining
                        this[KcalTrackWidget.targetKcalKey] = tdee
                        this[KcalTrackWidget.intakeKcalKey] = foodKcal
                    }
                }
                KcalTrackWidget().update(context, glanceId)
            }
        }
    }
}