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
    private static String TAG = "LogThis";
    private static File logFile = null;
    private static long startTimeLog;
    private static boolean logcat = true;
    private static boolean logToFile = true;

//    public static void reset(Activity activity) {
//        logFile = new File(activity.getExternalFilesDir(null), "LogThis.txt");
//        try {
//            logFile.delete();
//            logFile.createNewFile();
//            Calendar rightNow = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
//            appendLog(sdf.format(rightNow.getTime()));
//            startTimeLog = rightNow.getTimeInMillis();
//
//
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(logFile);
//            intent.setData(uri);
//            activity.sendBroadcast(intent);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static synchronized void i(String TAG, String message) {
        if (logcat) {
            Log.i(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Info : " + message);
        }
    }

    public static synchronized void d(String TAG, String message) {
        if (logcat) {
            Log.d(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Debug: " + message);
        }
    }

    public static synchronized void w(String TAG, String message) {
        if (logcat) {
            Log.w(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " Warn : " + message);
        }
    }

    public static synchronized void e(String TAG, String message) {
        if (logcat) {
            Log.e(TAG, message);
        }
        if (logToFile) {
            appendLog(TAG + " *** Error: " + message);
        }
    }

    public static void createLog(Activity activity, boolean LogCat, boolean LogToFile) {
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
                if (logFile.length() > 1024 * 1024) {
                    logFile.delete();
                    logFile.createNewFile();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
                Calendar rightNow = Calendar.getInstance();
                appendLog("\n"+sdf.format(rightNow.getTime()) + " " + logFile.toString());
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

    private static void appendLog(String text) {
        try {
            //BufferedWriter for performance, true to set append to file flag
            if (logFile != null) {
                if (logFile.length() > 1024 * 1024) {
                    logFile.delete();
                    logFile.createNewFile();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
                    Calendar rightNow = Calendar.getInstance();
                    appendLog(sdf.format(rightNow.getTime()) + " " + logFile.toString());
                }
                Calendar rightNow = Calendar.getInstance();
                long currentTime = rightNow.getTimeInMillis();
                long timeSinceStart = currentTime - startTimeLog;

                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(String.format("%10d ms : %s", timeSinceStart, text));
                buf.newLine();
                buf.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
