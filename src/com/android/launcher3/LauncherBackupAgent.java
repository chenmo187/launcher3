package com.android.launcher3;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.launcher3.logging.FileLog;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.recentTaskUtils.LauncherKeyManager;
import com.carsyso.mainsdk.manager.ChatRoomManager;
import com.carsyso.mainsdk.manager.InitParams;
import com.carsyso.mainsdk.manager.MainSDKInitializer;
import com.carsyso.mainsdk.manager.SmartBoxCarPlayManager;

public class LauncherBackupAgent extends BackupAgent {

private static String TAG = "LauncherBackupAgent";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate------");
        // Set the log dir as LauncherAppState is not initialized during restore.
        FileLog.setDir(getFilesDir());


    }


    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) {
        // Doesn't do incremental backup/restore
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        // Doesn't do incremental backup/restore
    }

    @Override
    public void onRestoreFinished() {
        RestoreDbTask.setPending(this, true);
    }
}
