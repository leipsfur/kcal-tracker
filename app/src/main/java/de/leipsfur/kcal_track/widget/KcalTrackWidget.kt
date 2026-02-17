package de.leipsfur.kcal_track.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import de.leipsfur.kcal_track.MainActivity
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

class KcalTrackWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()
        val remaining = prefs[remainingKcalKey] ?: 0

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Übrig",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
            
            Spacer(modifier = GlanceModifier.height(4.dp))
            
            Text(
                text = "$remaining",
                style = TextStyle(
                    color = if (remaining >= 0) GlanceTheme.colors.primary else GlanceTheme.colors.error,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = "kcal",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Quick Add Button (opens app)
            Row(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(8.dp)
                    .clickable(
                        actionStartActivity<MainActivity>(
                            actionParametersOf(quickAddKey to true)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text(
                     text = "+",
                     style = TextStyle(
                         color = GlanceTheme.colors.onPrimaryContainer,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold
                     )
                 )
                 Spacer(modifier = GlanceModifier.width(4.dp))
                 Text(
                     text = "Essen",
                     style = TextStyle(
                         color = GlanceTheme.colors.onPrimaryContainer,
                         fontSize = 12.sp
                     )
                 )
            }
        }
    }

    companion object {
        val remainingKcalKey = intPreferencesKey("remaining_kcal")
        val targetKcalKey = intPreferencesKey("target_kcal")
        val intakeKcalKey = intPreferencesKey("intake_kcal")
        val quickAddKey = ActionParameters.Key<Boolean>("quick_add")
    }
}