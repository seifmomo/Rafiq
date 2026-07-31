package com.example.rafiq.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object BackupManager {

    private const val BACKUP_DIR = "RAFIQ_Backup"
    private const val DATASTORE_FILE = "user_preferences.preferences_pb"
    private const val BACKUP_FILE = "rafiq_backup.zip"

    fun backupData(context: Context): Result<String> {
        return try {
            val backupDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                BACKUP_DIR
            )
            backupDir.mkdirs()

            val datastoreFile = File(context.filesDir.parentFile, "datastore/$DATASTORE_FILE")
            if (datastoreFile.exists()) {
                val destFile = File(backupDir, DATASTORE_FILE)
                datastoreFile.copyTo(destFile, overwrite = true)
            }

            val dbFile = context.getDatabasePath("rafiq_db")
            if (dbFile?.exists() == true) {
                val destDb = File(backupDir, "rafiq_db.db")
                dbFile.copyTo(destDb, overwrite = true)
            }

            val prefsFile = File(context.filesDir.parentFile, "shared_prefs/rafiq_locale.xml")
            if (prefsFile.exists()) {
                val destPrefs = File(backupDir, "rafiq_locale.xml")
                prefsFile.copyTo(destPrefs, overwrite = true)
            }

            Result.success(backupDir.absolutePath)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    fun restoreData(context: Context): Result<Unit> {
        return try {
            val backupDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                BACKUP_DIR
            )
            if (!backupDir.exists()) {
                return Result.failure(IOException("Backup directory not found"))
            }

            val datastoreFile = File(backupDir, DATASTORE_FILE)
            if (datastoreFile.exists()) {
                val destFile = File(context.filesDir.parentFile, "datastore/$DATASTORE_FILE")
                destFile.parentFile?.mkdirs()
                datastoreFile.copyTo(destFile, overwrite = true)
            }

            val dbFile = File(backupDir, "rafiq_db.db")
            if (dbFile.exists()) {
                val destDb = context.getDatabasePath("rafiq_db")
                destDb?.parentFile?.mkdirs()
                if (destDb != null) {
                    dbFile.copyTo(destDb, overwrite = true)
                }
            }

            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
