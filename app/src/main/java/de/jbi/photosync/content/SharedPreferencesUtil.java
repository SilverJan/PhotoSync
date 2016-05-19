package de.jbi.photosync.content;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.jbi.photosync.R;
import de.jbi.photosync.domain.Folder;

/**
 * Created by Jan on 16.05.2016.
 */
public class SharedPreferencesUtil {
    private static final String META_LAST_SYNC = "LAST_SYNC";

    /**
     * Recieves the FolderID <-> AbsolutePath maps and creates a List of new Folders (with the old ID)
     * @param ctx
     * @return
     */
    public static List<Folder> getFolders(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);

        List<Folder> folderList = new ArrayList<>();
        Map<String, ?> folderMaps = sharedPref.getAll();

        for (String key : folderMaps.keySet()) {
            File folderFile = new File((String) folderMaps.get(key));
            Folder folder = new Folder(folderFile, true);
            // Set ID to new Folder object
            folder.setId(UUID.fromString(key));
            folderList.add(folder);
        }

        return folderList;
    }

    // TODO add check on existence!
    public static void addFolder(Context ctx, Folder folder) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(folder.getId().toString(), folder.getAbsolutePath().getAbsolutePath());
        editor.commit();
    }

    public static void removeFolder(Context ctx, Folder folder) {
        if (!getFolders(ctx).contains(folder)) {
            String msg = "Folder was not found in local storage: \n"
                    + "{folder}: " + folder + "\n"
                    + "{localStorage folders}: " + getFolders(ctx);
            throw new IllegalArgumentException(msg);
            // TODO dafuq
        }

        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(folder.getId().toString());
        editor.commit();

        List<Folder> bla = getFolders(ctx);
    }

    public static void cleanFolderStorage(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void addMetaData(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_meta), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(META_LAST_SYNC, new Date(System.currentTimeMillis()).toString());
    }

    public static String getMetaData(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_meta), Context.MODE_PRIVATE);
        return sharedPref.getString(META_LAST_SYNC, new Date(System.currentTimeMillis()).toString());
    }
}
