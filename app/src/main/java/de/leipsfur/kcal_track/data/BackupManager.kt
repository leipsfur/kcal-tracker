package de.leipsfur.kcal_track.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

class BackupManager(private val context: Context) {

    private val dbName = "kcal_track.db"

    fun createBackup(): Uri {
        val db = KcalTrackDatabase.getInstance(context)

        // Flush WAL to main database file (must use query(), not execSQL(), for PRAGMA in Room)
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(TRUNCATE)").close()

        val dbFile = context.getDatabasePath(dbName)
        val backupDir = File(context.cacheDir, "backups")
        backupDir.mkdirs()

        val dateStr = LocalDate.now().toString()
        val backupFile = File(backupDir, "kcal-track-backup-$dateStr.db")
        dbFile.copyTo(backupFile, overwrite = true)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )
    }

    fun createShareIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun copyToStagingFile(uri: Uri): File {
        val stagingFile = File(context.cacheDir, "restore-staging.db")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(stagingFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Backup-Datei konnte nicht gelesen werden")
        return stagingFile
    }

    fun validateBackup(stagingFile: File): String? {
        return try {
            stagingFile.inputStream().use { stream ->
                val header = ByteArray(16)
                val bytesRead = stream.read(header)
                if (bytesRead < 16) {
                    return "Ungültige Backup-Datei"
                }
                val headerStr = String(header, Charsets.US_ASCII)
                if (!headerStr.startsWith("SQLite format 3")) {
                    return "Ungültige Backup-Datei (kein SQLite-Format)"
                }
                null
            }
        } catch (e: Exception) {
            "Fehler beim Lesen der Backup-Datei: ${e.message}"
        }
    }

    fun restoreBackup(stagingFile: File) {
        val db = KcalTrackDatabase.getInstance(context)
        db.close()

        val dbFile = context.getDatabasePath(dbName)
        File(dbFile.path + "-shm").delete()
        File(dbFile.path + "-wal").delete()

        stagingFile.copyTo(dbFile, overwrite = true)
        stagingFile.delete()
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        if (intent != null) {
            context.startActivity(intent)
        }
        Runtime.getRuntime().exit(0)
    }
}
