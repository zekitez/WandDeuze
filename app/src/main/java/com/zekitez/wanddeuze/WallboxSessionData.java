package com.zekitez.wanddeuze;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WallboxSessionData {

    private final String VALUES_AS_PREFS = "ValuesAsPrefs";
    private final String TAG = "WallboxSessionData";

    private long totalSessions;
    private long chargingTime;
    private long totalEnergy;
    private long prevTotalEnergy;
    private long deltaEnergy;
    private Context context;
    private final WallboxPulsarPlus wallbox;

    private String prevMonthsTotal = "";
    private String prevTodaysTotal = "";

    public WallboxSessionData(Context context, WallboxPulsarPlus wallbox){
        this.context = context;
        this.wallbox = wallbox;
        SharedPreferences values = context.getSharedPreferences(VALUES_AS_PREFS, 0);

        totalSessions = values.getLong(context.getString(R.string.key_totalSessions), 0);
        chargingTime = values.getLong(context.getString(R.string.key_chargingTime), 0);
        totalEnergy = values.getLong(context.getString(R.string.key_totalEnergy), 0);
        prevTotalEnergy = values.getLong(context.getString(R.string.key_prevTotalE), 0);
        deltaEnergy = values.getLong(context.getString(R.string.key_deltaEnergy), 0);

    }

    //--------------------------

    public void changeLanguage(Context context){
        // This was needed to get the language right when login fails.
        this.context = context;
    }

    public String dataFromLockListener(JSONObject resume) throws JSONException {
        if (resume != null) {
            LogThis.d(TAG, "dataFromLockListener: newValues resume:" + resume);
            if (resume.has(wallbox.CD_RESUME_TOTALSESSIONS)) {
                if (totalSessions != resume.getLong(wallbox.CD_RESUME_TOTALSESSIONS)) {
                    totalSessions = resume.getLong(wallbox.CD_RESUME_TOTALSESSIONS);
                    if (totalSessions == 0) {
                        chargingTime = 0;
                        prevTotalEnergy = totalEnergy;
                        totalEnergy = 0;
                        // deltaEnergy = 0;
                        LogThis.d(TAG, "dataFromLockListener: totalSessions == 0  RESET VALUES");

                    } else {
                        if (resume.has(wallbox.CD_RESUME_CHARGINGTIME)) {
                            chargingTime = resume.getLong(wallbox.CD_RESUME_CHARGINGTIME);
                        }
                        if (resume.has(wallbox.CD_RESUME_TOTALENERGY)) {
                            long value = resume.getLong(wallbox.CD_RESUME_TOTALENERGY);
                            long newDeltaEnergy = value - totalEnergy;
                            if (newDeltaEnergy > 0) {
                                deltaEnergy = newDeltaEnergy;
                            }
                            totalEnergy = value;
                        }
                    }
                    saveChargeSessions();

                } else{
                    LogThis.d(TAG, "dataFromLockListener: no change totalSessions " + totalSessions + " " + resume.getLong(wallbox.CD_RESUME_TOTALSESSIONS));
                }
            } else{
                LogThis.d(TAG, "dataFromLockListener: missing totalSessions");
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat( " MMMM", context.getResources().getConfiguration().locale);
        Calendar rightNow = Calendar.getInstance();

        String monthsTotal = formatDouble(deltaEnergy / 1000.0, 3);
        monthsTotal = context.getString(R.string.last_session) + monthsTotal + " kWh \n";
        monthsTotal = monthsTotal + sdf.format(rightNow.getTime()) + " total (" + totalSessions + "): " + formatDouble(totalEnergy / 1000.0, 3) + " kWh";

        if (!prevMonthsTotal.equals(monthsTotal)) {
            LogThis.d(TAG, "dataFromLockListener: " + monthsTotal.replace("\n",""));
            prevMonthsTotal = monthsTotal;
        }

        return monthsTotal;
    }

    //--------------------------

    public String dataFromStateListener(JSONObject state) throws JSONException {
        if (state.has(wallbox.STATUS_ADDED_ENERGY) ){
            // LogThis.d(TAG, "dataFromStateListener added_energy " + state.getDouble(wallbox.STATUS_ADDED_ENERGY));
            long addedEnergy = (long) (state.getDouble(wallbox.STATUS_ADDED_ENERGY) * 1000);
            String todaysTotal = context.getString(R.string.todays_total) + formatDouble(addedEnergy / 1000.0, 3) + " kWh";
            if ( !prevTodaysTotal.equals(todaysTotal)) {
                LogThis.d(TAG, "dataFromStateListener: " + todaysTotal);
                prevTodaysTotal = todaysTotal;
            }
            return (todaysTotal);
        }
        LogThis.d(TAG, "dataFromStateListener: Missing field added_energy ");
        return context.getString(R.string.missing_attribute) + wallbox.STATUS_ADDED_ENERGY;
    }

    //---------------------------

    private void saveChargeSessions(){
        SharedPreferences values = context.getSharedPreferences(VALUES_AS_PREFS, 0);
        SharedPreferences.Editor editor = values.edit();

        editor.putLong(context.getString(R.string.key_totalSessions), totalSessions);
        editor.putLong(context.getString(R.string.key_chargingTime), chargingTime);
        editor.putLong(context.getString(R.string.key_totalEnergy), totalEnergy);
        editor.putLong(context.getString(R.string.key_prevTotalE), prevTotalEnergy);
        editor.putLong(context.getString(R.string.key_deltaEnergy), deltaEnergy);
        editor.commit();
    }

    //------------------------------

    private String formatDouble(double value, int digits){
        DecimalFormat decimalFormat;
        StringBuilder pattern = new StringBuilder("0");
        if (digits > 0) {
            pattern.append(".0");
        }
        while ( digits > 1 ){
            pattern.append("0");
            digits = digits - 1 ;
        }
        decimalFormat = new DecimalFormat(pattern.toString());
        return decimalFormat.format(value);
    }


}
