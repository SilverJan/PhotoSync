package de.jbi.photosync.utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by Jan on 12.06.2016.
 */
public class BackupAgent extends BackupAgentHelper{
    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "myfiles";

    @Override
    public void onCreate() {
        Logger.showInAndroidLog("Auto backup active");
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, Constants.SHARED_PREFERENCES_DATA, Constants.SHARED_PREFERENCES_META);
        addHelper(FILES_BACKUP_KEY, helper);
    }

    public static void requestAppBackup() {
        BackupManager bm = new BackupManager(AndroidUtil.ContextHandler.getMainContext());
        bm.dataChanged();
    }
}
