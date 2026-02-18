package de.leipsfur.kcal_track.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.*
import de.leipsfur.kcal_track.KcalTrackApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

object KcalTrackWidgetManager {
    private const val TAG = "KcalTrackWidgetManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateWidget(context: Context) {
        scope.launch {
            try {
                val app = context.applicationContext as KcalTrackApplication
                val db = app.database
                val today = LocalDate.now()

                val foodKcal = db.foodEntryDao().getTotalKcalForDate(today).first()
                val activityKcal = db.activityEntryDao().getTotalKcalForDate(today).first()
                val bmr = app.settingsRepository.getBmrForDate(today).first() ?: 0
                val tdee = bmr + activityKcal
                val remaining = tdee - foodKcal

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget", e)
            }
        }
    }
}
