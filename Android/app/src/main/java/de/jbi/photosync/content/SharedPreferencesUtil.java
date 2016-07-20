package de.jbi.photosync.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Logger;

import static de.jbi.photosync.utils.Constants.SHARED_PREFERENCES_DATA;
import static de.jbi.photosync.utils.Constants.SHARED_PREFERENCES_META;

/**
 * Created by Jan on 16.05.2016.
 */
public class SharedPreferencesUtil {
    public static final String META_LAST_SYNC_TIME = "LAST_SYNC_TIME";
    public static final String META_LAST_SYNC_NEW_FOLDERS = "LAST_SYNC_FOLDERS";
    public static final String META_LAST_SYNC_NEW_FILES = "LAST_SYNC_FILES";
    public static final String META_LAST_SYNC_MISSING_FILES = "LAST_SYNC_MISSING_FILES";

    private static Context ctx = AndroidUtil.ContextHandler.getMainContext();


    /**
     * Recieves the FolderID <-> AbsolutePath maps and creates a List of new Folders (with the old ID)
     *
     * @return
     */
    public static List<Folder> getFolders() {
        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_DATA, Context.MODE_PRIVATE);

        List<Folder> folderList = new ArrayList<>();
        Map<String, ?> folderMaps = sharedPref.getAll();

        for (String key : folderMaps.keySet()) {
            String value = (String) folderMaps.get(key);
            Folder folder;
            try {
                folder = Folder.deserializeGson(value);
            } catch (JsonSyntaxException ex) {
                // Remove this maybe, actually should never happen anymore. Removed while adding renaming feature
                File folderFile = new File(value);
                folder = new Folder(folderFile, true);
                Logger.getInstance().appendLog("Remove this folder and re-add it to support renaming", true);
            }

            // Set ID to new Folder object
            folder.setId(UUID.fromString(key));
            folderList.add(folder);
        }

        return folderList;
    }

    public static void addFolder(Folder folder) {
        if (assertFolderExists(folder)) {
            String msg = "Folder already exists in local storage: \n"
                    + "{folder}: " + folder + "\n"
                    + "{localStorage folders}: " + getFolders();
            throw new IllegalArgumentException(msg);
        }
        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(folder.getId().toString(), Folder.serializeGson(folder));
        editor.apply();
    }

    public static void removeFolder(Folder folder) {
        assertFolderExistsAndThrow(folder);

        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(folder.getId().toString());
        editor.apply();
    }

    public static void updateFolder(Folder updatedFolder) {
        assertFolderExistsAndThrow(updatedFolder);

        for (Folder folder : getFolders()) {
            if (folder.getId().equals(updatedFolder.getId())) {
                removeFolder(folder);
                addFolder(updatedFolder);
                return;
            }
        }
    }

    public static void cleanFolderStorage() {
        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Adds meta data of recent sync to SharedPref
     */
    public static void addMetaData(Date timeStamp, int folderAmount, int fileAmount, int missingAmount) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_META, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(META_LAST_SYNC_TIME, timeStamp.toString());
        editor.putString(META_LAST_SYNC_NEW_FOLDERS, Integer.toString(folderAmount));
        editor.putString(META_LAST_SYNC_NEW_FILES, Integer.toString(fileAmount));
        editor.putString(META_LAST_SYNC_MISSING_FILES, Integer.toString(missingAmount));
        editor.apply();
    }

    /**
     * Returns meta data of last sync
     *
     * @return Map with last sync time, last folder amount, last file amount, last missing file amount
     */
    public static Map<String, String> getMetaData() {
        SharedPreferences sharedPref = ctx.getSharedPreferences(SHARED_PREFERENCES_META, Context.MODE_PRIVATE);

        Map<String, String> metas = new HashMap<>();
        metas.put(META_LAST_SYNC_TIME, sharedPref.getString(META_LAST_SYNC_TIME, new Date(System.currentTimeMillis()).toString()));
        metas.put(META_LAST_SYNC_NEW_FOLDERS, sharedPref.getString(META_LAST_SYNC_NEW_FOLDERS, "0"));
        metas.put(META_LAST_SYNC_NEW_FILES, sharedPref.getString(META_LAST_SYNC_NEW_FILES, "0"));
        metas.put(META_LAST_SYNC_MISSING_FILES, sharedPref.getString(META_LAST_SYNC_MISSING_FILES, "0"));
        return metas;
    }

    public static String getAnyStringValue(String prefKey) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getString(prefKey, "");
    }

    public static Boolean getAnyBooleanValue(String prefKey) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getBoolean(prefKey, false);
    }

    /**
     * Sets all the settings to default values, be careful with that!
     */
    public static void setSettingsToDefaultValues() {
        PreferenceManager.setDefaultValues(ctx, R.xml.preferences, true);
    }

    private static void assertFolderExistsAndThrow(Folder folder) {
        if (!assertFolderExists(folder)) {
            String msg = "Folder was not found in local storage: \n"
                    + "{folder}: " + folder + "\n"
                    + "{localStorage folders}: " + getFolders();
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean assertFolderExists(Folder folder) {
        return getFolders().contains(folder);
    }
}
