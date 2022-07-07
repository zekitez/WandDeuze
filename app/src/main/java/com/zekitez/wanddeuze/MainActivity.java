package com.zekitez.wanddeuze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.sdsmdg.harjot.croller.Croller;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;
import android.os.Process;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements WallboxResultListener {

    private final String TAG = "MainActivity";
    
    private String chargerId = "12345";
    private Boolean displayNbrChargerStatusses = false;

    private TextView textViewMessage;
    private CheckBox checkBoxConnected;
    private CheckBox checkBoxPluggedIn;

    private RadioGroup radioGroupLocked;
    private RadioButton radioButtonLock;
    private RadioButton radioButtonUnLock;

    private RadioGroup radioGroupAction;
    private RadioButton radioButtonPauze;
    private RadioButton radioButtonResume;

    private SwitchCompat enableCurrentChangeSwitch;
    private Croller croller;

    private WallboxPulsarPlus wallbox;
    private ScheduledExecutorService timer = null;

    public GlobalFunctions globalFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPrefs(false);
        LogThis.d(TAG, "onCreate");

        globalFunctions = (GlobalFunctions) getApplicationContext();
        wallbox = new WallboxPulsarPlus(this);

        setContentView(R.layout.main_activity);
        try{
            Context context = getApplicationContext();
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            setTitle(getString(R.string.app_name) + "  " + versionName );
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewMessage = findViewById(R.id.textViewMessage);
        checkBoxConnected = findViewById(R.id.checkboxConnected);
        checkBoxPluggedIn = findViewById(R.id.checkboxPluggedIn);

        radioGroupLocked = findViewById(R.id.RadioGroupLocked);
        radioButtonLock = findViewById(R.id.radioButtonLock);
        radioButtonUnLock = findViewById(R.id.radioButtonUnlock);

        radioGroupAction = findViewById(R.id.RadioGroupAction);
        radioButtonPauze = findViewById(R.id.radioButtonPauze);
        radioButtonResume = findViewById(R.id.radioButtonResume);

        radioButtonLock.setOnClickListener(lockOnClickListener);
        radioButtonUnLock.setOnClickListener(unLockOnClickListener);
        radioButtonPauze.setOnClickListener(pauzeOnClickListener);
        radioButtonResume.setOnClickListener(resumeOnClickListener);

        enableCurrentChangeSwitch = findViewById(R.id.enableCurrentChangeSwitch);
        enableCurrentChangeSwitch.setOnCheckedChangeListener(enableCurrentChangeListener);

        croller = findViewById(R.id.croller);

        setRadioButtonsLockedUnlocked(false,Color.GRAY );
        setRadioButtonsPauseResume(false,Color.GRAY );

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogThis.d(TAG, "onResume");
        wallbox.destroyTimer();
        destroySlowDownTimer();
        getPrefs(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogThis.d(TAG, "onStop");
        wallbox.destroyTimer();
        destroySlowDownTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogThis.d(TAG, "onDestroy");
        wallbox.destroyTimer();
        destroySlowDownTimer();
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogThis.d(TAG, "onPause");
        wallbox.destroyTimer();
        destroySlowDownTimer();
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onBackPressed(){
        LogThis.d(TAG, "onBackPressed");
        new AlertDialog.Builder(this)
                .setTitle(R.string.txt_really_exit)
                .setMessage(R.string.txt_are_you_sure_exit)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        LogThis.d(TAG, "onBackPressed YES");
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogThis.d(TAG, "onConfigurationChanged");
    }
    //-----

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_prefs) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return (true);

        } else if (item.getItemId() == R.id.action_disclaimer){
            new AcceptDeclineDialog(MainActivity.this, R.layout.disclaimer, globalFunctions.getDisclaimerTxt());

        } else if (item.getItemId() == R.id.action_privacy_policy){
            new AcceptDeclineDialog(MainActivity.this, R.layout.privacy_policy, globalFunctions.getPrivicyPolicyTxt());
        }
        return (super.onOptionsItemSelected(item));
    }


    //-----------

    public void getPrefs(boolean askAcceptance) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString(getString(R.string.key_username), "unknown");
        String password = prefs.getString(getString(R.string.key_password), "unknown");
        int connectionTimeOutSec = Integer.parseInt(prefs.getString(getString(R.string.key_connectionTimeOut), "4"));
        chargerId = prefs.getString(getString(R.string.key_chargerId), "unknown");
        displayNbrChargerStatusses = prefs.getBoolean(getString(R.string.key_displayNbrChargerStats), false);
        boolean logcat = prefs.getBoolean(getString(R.string.key_logcat), false);
        boolean logToFile = prefs.getBoolean(getString(R.string.key_logToFile), false);
        LogThis.createLog(this, logcat, logToFile);

        if (askAcceptance) {
            boolean value = prefs.getBoolean(getString(R.string.key_privacyPolycyAccepted), false);
            if (value == false) {
                new AcceptDeclineDialog(MainActivity.this, R.layout.privacy_policy, globalFunctions.getPrivicyPolicyTxt());
            }
            value = prefs.getBoolean(getString(R.string.key_disclaimerAccepted), false);
            if (value == false) {
                new AcceptDeclineDialog(MainActivity.this, R.layout.disclaimer, globalFunctions.getDisclaimerTxt());
            }
            if (wallbox != null) {
                wallbox.connectToWallbox(username, password, Math.abs(connectionTimeOutSec * 1000));
            }
        }
        // LogThis.d(TAG, "connection timeout " + connectionTimeOutSec);
    }

    //----------- GUI listeners

    // A listener per radioButton results in one call per button push.
    // The radioGroup onCheckGhangedListener triggers multiple calls per button push.

    public final View.OnClickListener lockOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "lockOnClickListener");
            radioButtonUnLock.setTextColor(Color.WHITE);
            radioButtonLock.setTextColor(Color.WHITE);
            wallbox.setWallboxLock(chargerId, true);
        }
    };

    public final View.OnClickListener unLockOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "un-LockOnClickListener");
            radioButtonUnLock.setTextColor(Color.WHITE);
            radioButtonLock.setTextColor(Color.WHITE);
            wallbox.setWallboxLock(chargerId, false);
        }
    };

    public final View.OnClickListener pauzeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "pauzeOnClickListener");
            radioButtonResume.setEnabled(true);
            wallbox.setWallboxAction(chargerId,true);
        }
    };

    public final View.OnClickListener resumeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "resumeOnClickListener");
            wallbox.setWallboxAction(chargerId,false);
        }
    };

    int progress = 0;
    public final CompoundButton.OnCheckedChangeListener enableCurrentChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checkedChanged) {
            croller.setEnabled(checkedChanged);
            enableCurrentChangeSwitch.setTextColor(Color.WHITE);
            LogThis.d(TAG, "enableCurrentChangeListener " + checkedChanged);
            if (checkedChanged) {
                wallbox.destroyTimer(); // No updates during current change.
                progress = croller.getProgress();
            } else {
                if ( progress != croller.getProgress()){
                    progress = croller.getProgress();
                    wallbox.setWallboxMaxChargingCurrent(chargerId, progress);
                }
            }
        }
    };

    // ---------- Timer --------------------

    private void destroySlowDownTimer() {
        if (timer != null) {
            timer.shutdown();
            timer = null;
        }
    }

    private void createSlowDownTimer(int startDelay){
        LogThis.d(TAG, "createSlowDownTimer startDelay:" + startDelay);
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(new SlowDownStateRequests(), startDelay, 1000, TimeUnit.MILLISECONDS);
    }

    private class SlowDownStateRequests implements Runnable {
        @Override
        public void run() {
            LogThis.d(TAG, "SlowDownStateRequests RUN");
            wallbox.getWallboxState(chargerId, 1000, 5000);
            destroySlowDownTimer();
        }
    }

    //------- Common -----------------------

    int count = 1;

    private void handleStatus(int status, String description){
        if (displayNbrChargerStatusses) {
            textViewMessage.setText("" + count + ".   " + description);
        } else {
        textViewMessage.setText(description);
        }
        count ++;
        if (status == WallBoxStatus.STATUS_NOT_CONNECTED ||
                status == WallBoxStatus.STATUS_READY ||
                status == WallBoxStatus.STATUS_LOCKED ) {
            checkBoxPluggedIn.setChecked(false);
            radioButtonPauze.setTextColor(Color.GRAY);
            radioButtonPauze.setEnabled(false);
            radioButtonResume.setTextColor(Color.GRAY);
            radioButtonResume.setEnabled(false);

        } else {
            checkBoxPluggedIn.setChecked(true);
            if (status == WallBoxStatus.STATUS_CHARGING ) {                  // 194 = charging then only allow pauze.
            radioButtonPauze.setTextColor(Color.WHITE);
            radioButtonPauze.setEnabled(true);
            radioButtonResume.setTextColor(Color.GRAY);
            radioButtonResume.setEnabled(false);
        } else {
            radioButtonPauze.setTextColor(Color.GRAY);
            radioButtonPauze.setEnabled(false);
            radioButtonResume.setTextColor(Color.WHITE);
            radioButtonResume.setEnabled(true);
        }
    }
        setRadioButtonsLockedUnlocked(true, Color.WHITE);
    }


    private void handleLocked(int lockedState, int status, boolean fromChangeListener) {
        int backCircleColor ;

        if (lockedState == 1) {  // Locked ?
            radioGroupLocked.check(R.id.radioButtonLock);
            if (fromChangeListener) {
                radioButtonLock.setTextColor(Color.GREEN);
            }
//            croller.setBackCircleDisabledColor(getResources().getColor(R.color.locked));

        } else if (lockedState == 0) {  // UnLocked ?
            radioGroupLocked.check(R.id.radioButtonUnlock);
            if (fromChangeListener) {
                radioButtonUnLock.setTextColor(Color.GREEN);
            }

        } else {  // Unknown
            radioGroupLocked.clearCheck();
            radioButtonUnLock.setTextColor(Color.RED);
            radioButtonLock.setTextColor(Color.RED);
        }

        LogThis.e(TAG,"handleLocked status="+status);
        switch (status) {
            case WallBoxStatus.STATUS_NOT_CONNECTED:
                backCircleColor = getResources().getColor(R.color.red);
                break;
            case WallBoxStatus.STATUS_READY:
                backCircleColor = getResources().getColor(R.color.ready);
                break;
            case WallBoxStatus.STATUS_WAIT_NEXT_SCHEDULE_8:
            case WallBoxStatus.STATUS_WAIT_NEXT_SCHEDULE_9:
                backCircleColor = getResources().getColor(R.color.waitingNextSchedule);
                break;
            case WallBoxStatus.STATUS_WAIT_CAR_DEMAND_0:
            case WallBoxStatus.STATUS_WAIT_CAR_DEMAND_1:
                backCircleColor = getResources().getColor(R.color.waitingCarDemand);
                break;
            case WallBoxStatus.STATUS_PAUSED:
                backCircleColor = getResources().getColor(R.color.paused);
                break;
            case WallBoxStatus.STATUS_CHARGING:
                backCircleColor = getResources().getColor(R.color.charging);
                break;
            case WallBoxStatus.STATUS_LOCKED:
            case WallBoxStatus.STATUS_WAIT_UNLOCK:
                backCircleColor = getResources().getColor(R.color.locked);
                break;
            default:
                backCircleColor = getResources().getColor(R.color.red);
                break;
        }
        croller.setBackCircleDisabledColor(backCircleColor);

    }


    private String getTitle(JSONObject object, String description, String status) throws JSONException {

        String title = object.getString(wallbox.NAME) + "\n";
        if (object.has(description)) {
            title = title + object.getString(description);
        } else {
            if (object.has(status)) {
                int state = object.getInt(status);
                switch (state) {
                    case WallBoxStatus.STATUS_NOT_CONNECTED :
                        title = title + getString(R.string.status_not_connected);
                        break;
                    case WallBoxStatus.STATUS_READY:
                        title = title + getString(R.string.status_ready);
                        break;
                    case WallBoxStatus.STATUS_WAIT_NEXT_SCHEDULE_8:
                    case WallBoxStatus.STATUS_WAIT_NEXT_SCHEDULE_9:
                        title = title + getString(R.string.status_wait_next_schedule);
                        break;
                    case WallBoxStatus.STATUS_WAIT_CAR_DEMAND_0:
                    case WallBoxStatus.STATUS_WAIT_CAR_DEMAND_1:
                        title = title + getString(R.string.status_wait_car_demand);
                        break;
                    case WallBoxStatus.STATUS_PAUSED:
                        title = title + getString(R.string.status_paused);
                        break;
                    case WallBoxStatus.STATUS_CHARGING:
                        title = title + getString(R.string.status_charging);
                        break;
                    case WallBoxStatus.STATUS_LOCKED:
                        title = title + getString(R.string.status_locked);
                        break;
                    case WallBoxStatus.STATUS_WAIT_UNLOCK:
                        title = title + getString(R.string.status_wait_unlock);
                        break;
                    default:
                        title = title + getString(R.string.status_unknown) + state;
                        break;
                }
            } else {
                title = getString(R.string.status_attrs_missing);
            }
        }
        return title;
    }

    private void setRadioButtonsPauseResume(boolean enabled, int color){
        radioButtonPauze.setEnabled(enabled);
        radioButtonResume.setEnabled(enabled);
        radioButtonPauze.setTextColor(color);
        radioButtonResume.setTextColor(color);
        if ( !enabled ) {
            radioGroupAction.clearCheck();
        }
    }

    private void setRadioButtonsLockedUnlocked(boolean enabled, int color){
        radioButtonLock.setEnabled(enabled);
        radioButtonUnLock.setEnabled(enabled);
        radioButtonLock.setTextColor(color);
        radioButtonUnLock.setTextColor(color);
        if ( !enabled ) {
            radioGroupLocked.clearCheck();
        }
        enableCurrentChangeSwitch.setEnabled(enabled);
        enableCurrentChangeSwitch.setTextColor(color);
    }

    //------- Implemented Wallbox listeners

    @Override
    public synchronized void wallboxConnectedListener(boolean connected, String text) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallboxConnectedListener " + connected + " " + text);
            checkBoxConnected.setChecked(connected);
            if (connected) {
                checkBoxConnected.setTextColor(getResources().getColor(R.color.white));
                textViewMessage.setText(R.string.some_message);
                wallbox.getWallboxState(chargerId, 2000, 2000);    // Start
                createSlowDownTimer(10000);
            } else {
                checkBoxConnected.setTextColor(getResources().getColor(R.color.red));
                textViewMessage.setText(text);
            }
        });
    }

    @Override
    public synchronized void wallboxStateListener(boolean resultReceived, JSONObject state) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallboxStateListener " + resultReceived + " " + state.toString());
            if (resultReceived) {
                try {
                    if (state.has("max_available_power")){
                        croller.setMax(state.getInt("max_available_power"));
                    } else {
                        croller.setMax(64);
                    }
                    if (state.has(wallbox.CONFIG_DATA)) {
                        JSONObject configData = state.getJSONObject(wallbox.CONFIG_DATA);
                        int status = state.getInt(wallbox.STATUS_ID);
                        handleStatus(status, getTitle(state, wallbox.STATE_STATUS_DESCRIPTION, wallbox.STATUS_ID));
                        handleLocked(configData.getInt(wallbox.LOCKED), status, false);

                        if (configData.has("remote_action")) {
                            if (configData.getInt("remote_action") == 1) {
                                radioGroupAction.check(R.id.radioButtonResume);
                            } else if (configData.getInt("remote_action") == 2) {
                                radioGroupAction.check(R.id.radioButtonPauze);
                            } else {                        // Remote_action  = 0
                                radioGroupAction.clearCheck();
                            }
//                        } else {
//                            radioGroupAction.clearCheck();
                        }
                        if (configData.has("max_charging_current")){
                            croller.setProgress(configData.getInt("max_charging_current"));
                        } else {
                            croller.setProgress(1);
                        }
                    } else {
                        textViewMessage.setText(R.string.no_configdata);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String text = "wallboxStateListener " + e.getMessage();
                    LogThis.e(TAG, text);
                    textViewMessage.setText(text);
                }
            } else {
                textViewMessage.setText(state.toString());
            }
        });
    }


    @Override
    public synchronized void wallboxLockChangeListener(boolean stateReceived, JSONObject response) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallboxLockChangeListener " + stateReceived + " " + response);
            if (stateReceived) {
                try {
                    if (response.has(wallbox.DATA)) {
                        JSONObject data = response.getJSONObject(wallbox.DATA);
                        if (data.has(wallbox.CHARGER_DATA)) {
                            JSONObject chargerData = data.getJSONObject(wallbox.CHARGER_DATA);
                            int status = chargerData.getInt(wallbox.STATUS);

                            handleStatus(status, getTitle(chargerData, wallbox.STATUS_DESCRIPTION, wallbox.STATUS));
                            handleLocked(chargerData.getInt(wallbox.LOCKED), status, true);

                            wallbox.getWallboxState(chargerId, 2000, 2000);  // Restart the timer
                            createSlowDownTimer(10000);
                        } else {
                            textViewMessage.setText(R.string.no_chargerdata);
                        }
                    } else {
                        textViewMessage.setText(R.string.data_not_received);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String text = "wallboxLockChangeListener " + e.getMessage();
                    LogThis.e(TAG, text);
                    textViewMessage.setText(text);
                }
            } else {
                textViewMessage.setText(response.toString());
            }
        });
    }


    @Override
    public void wallboxActionChangeListener(boolean stateReceived, JSONObject response) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallboxActionChangeListener " + stateReceived + " " + response);
            if (stateReceived) {
                try {
                    // The response has NO status.
                    if (response.has(wallbox.ACTION)) {
                        int action = response.getInt(wallbox.ACTION);
                        if (action == 1){    // RESUME ?
                            radioButtonPauze.setTextColor(Color.WHITE);
                            radioButtonResume.setTextColor(Color.GREEN);
                        } else if (action == 2){  // PAUZE ?
                            radioButtonPauze.setTextColor(Color.GREEN);
                            radioButtonResume.setTextColor(Color.WHITE);
                        } else if (action == 0){
                            radioGroupAction.clearCheck();
                        }
                    } else {
                        textViewMessage.setText(R.string.data_not_received);
                    }
                    wallbox.getWallboxState(chargerId, 2000, 2000);  // Restart the timer
                    createSlowDownTimer(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                    String text = "wallboxActionChangeListener " + e.getMessage();
                    LogThis.e(TAG, text);
                    textViewMessage.setText(text);
                }
            } else {
                textViewMessage.setText(response.toString());
            }
        });
    }


    @Override
    public void wallboxMaxChargingCurrentListener(boolean stateReceived, JSONObject response) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallBoxMaxChargingCurrentListener " + stateReceived + " " + response);
            if (stateReceived) {
                try {
                    if (response.has(wallbox.DATA)) {
                        JSONObject data = response.getJSONObject(wallbox.DATA);
                        if (data.has(wallbox.CHARGER_DATA)) {
                            JSONObject chargerData = data.getJSONObject(wallbox.CHARGER_DATA);
                            if ( chargerData.has("maxChargingCurrent") && progress == chargerData.getInt("maxChargingCurrent") ){
                                enableCurrentChangeSwitch.setTextColor(Color.GREEN);
                            } else {
                                enableCurrentChangeSwitch.setTextColor(Color.RED);
                            }
                        } else {
                            textViewMessage.setText(R.string.no_chargerdata);
                        }
                    } else {
                        textViewMessage.setText(R.string.data_not_received);
                    }
                    wallbox.getWallboxState(chargerId, 2000, 2000);  // Restart the timer
                    createSlowDownTimer(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                    String text = "wallBoxMaxChargingCurrentListener " + e.getMessage();
                    LogThis.e(TAG, text);
                    textViewMessage.setText(e.getMessage());
                }
            } else {
                textViewMessage.setText(response.toString());
            }
        });

    }

    @Override
    public void wallboxErrorListener(String error) {
        runOnUiThread(() -> {
            LogThis.d(TAG, "wallboxErrorListener: " + error);
            textViewMessage.setText(error);
        });

    }

    //---------------------------
}