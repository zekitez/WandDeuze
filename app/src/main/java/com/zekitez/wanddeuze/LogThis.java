package com.zekitez.wanddeuze;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogThis {
    private final static String TAG = "LogThis";
    private static File logFile = null;
    private static File prevLogFile = null;
    private static long startTimeLog;
    private static boolean logcat = true;
    private static boolean logToFile = true;

    private static final int MAX_SIZE_FILE = 4096000;

    public static void i(String TAG, String message) {
        if (logcat) {
            Log.i(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Info : " + message, false);
        }
    }

    public static void d(String TAG, String message) {
        if (logcat) {
            Log.d(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Debug: " + message, false);
        }
    }

    public static void w(String TAG, String message) {
        if (logcat) {
            Log.w(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Warn : " + message, false);
        }
    }

    public static void e(String TAG, String message) {
        if (logcat) {
            Log.e(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " *** Error: " + message, false);
        }
    }

    public static synchronized void createLog(Activity activity, boolean LogCat, boolean LogToFile) {
        if (LogCat) {
            logcat = true;
        } else {
            logcat = false;
        }
        if (LogToFile) {
            logToFile = true;
        } else {
            logToFile = false;
            logFile = null;
        }

        try {
            if (logToFile && logFile == null) {

                logFile = new File(activity.getExternalFilesDir(null), "LogThis.txt");
                prevLogFile = new File(activity.getExternalFilesDir(null), "LogThisPrev.txt");
                if (logFile.length() > MAX_SIZE_FILE) {
                    moveToPrevFile();
                    //logFile.delete();
                    logFile.createNewFile();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss:SSS");
                Calendar rightNow = Calendar.getInstance();
                appendLog(sdf.format(rightNow.getTime()) + " " + logFile.toString(), true);
                startTimeLog = rightNow.getTimeInMillis();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(logFile);
                intent.setData(uri);
                activity.sendBroadcast(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void appendLog(String text, boolean startLog) {
        try {
            //BufferedWriter for performance, true to set append to file flag
            if (logFile != null) {
                if (logFile.length() > MAX_SIZE_FILE) {
                    moveToPrevFile();
                    //logFile.delete();
                    logFile.createNewFile();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss:SSS");
                    Calendar rightNow = Calendar.getInstance();
                    startTimeLog = rightNow.getTimeInMillis();
                    appendLog(sdf.format(rightNow.getTime()) + " " + logFile.toString(), true);
                }
                Calendar rightNow = Calendar.getInstance();
                long currentTime = rightNow.getTimeInMillis();
                long timeSinceStart = currentTime - startTimeLog;

                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                if ( startLog ) {
                    buf.newLine();
                    buf.append(text);
                } else {
                    buf.append(String.format("%10d ms : %s", timeSinceStart, text));
                }
                buf.newLine();
                buf.flush();
                buf.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void moveToPrevFile(){
        if (prevLogFile.exists()){
            prevLogFile.delete();
            // Log.d(TAG,"moveToPrevFile delete prevLogFile");
        }
        logFile.renameTo(prevLogFile);
        if (logFile.exists()){
            logFile.delete();
            // Log.d(TAG,"moveToPrevFile delete logFile");
        }
        // Log.d(TAG,"logFile = "+logFile.toString());
    }
}
