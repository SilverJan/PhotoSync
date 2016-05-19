package de.jbi.photosync.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Observable;

/**
 * Created by Jan on 17.05.2016.
 */
public class Logger extends Observable{
    private static Logger ourInstance = new Logger();

    private static Context ctx;
    private String log;

    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
        log = "";
    }

    public static void setCtx(Context ctx) {
        Logger.ctx = ctx;
    }

    public String getLog() {
        return log;
    }

    public void appendLog(String message) {
        log += "\n";
        log += message;
        setChanged();
        notifyObservers(log);
        showLogToast(message);;
    }

    private void showLogToast(String msg) {
        // could fail, if ctx is null. ctx is set in MainActivity to secure its existence for the whole app lifecycle
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
