package com.zekitez.wanddeuze;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;
import android.os.Process;

public class

MainActivity extends AppCompatActivity implements WallboxResultListener {

    private final String TAG = "MainActivity";
    
    private String chargerId = "12345";

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
    private boolean logcat = false, logToFile = false;

    public GlobalFunctions globalFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalFunctions = (GlobalFunctions) getApplicationContext();

        getPrefs(false);

        wallbox = new WallboxPulsarPlus(this);

        setContentView(R.layout.main_activity);
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
        wallbox.destroyTimer();
        getPrefs(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wallbox.destroyTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wallbox.destroyTimer();
        finish();
        Process.killProcess(Process.myPid());
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

//    @Override
//    public void onBackPressed() {
//    }

    //-----------

    public void getPrefs(boolean askAcceptance) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString(getString(R.string.key_username), "unknown");
        String password = prefs.getString(getString(R.string.key_password), "unknown");
        int connectionTimeOutSec = Integer.parseInt(prefs.getString(getString(R.string.key_connectionTimeOut), "4"));
        chargerId = prefs.getString(getString(R.string.key_chargerId), "unknown");
        logcat = prefs.getBoolean(getString(R.string.key_logcat), false);
        logToFile = prefs.getBoolean(getString(R.string.key_logToFile), false);
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
                wallbox.connectToWallbox(username, password, connectionTimeOutSec);
            }
        }
        LogThis.d(TAG, "connection timeout " + connectionTimeOutSec);
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
        public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
            croller.setEnabled(bool);
            enableCurrentChangeSwitch.setTextColor(Color.WHITE);
            LogThis.d(TAG,"enableCurrentChangeListener " + bool);
            if (bool){
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

    //------- Common

    private void handleStatus(int status, String description){
        textViewMessage.setText(description);
        if (status == 161 || status == 209){  // 161 = ready, 209 = Locked
            checkBoxPluggedIn.setChecked(false);
        } else {
            checkBoxPluggedIn.setChecked(true);
        }
        setRadioButtonsLockedUnlocked(true,Color.WHITE);
        if (status == 194){                  // 194 = charging then only allow pauze.
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

    private String getTitle(JSONObject object, String description, String status) throws JSONException {
        String title = "";
        if (object.has(description)) {
            title = object.getString(wallbox.NAME) + "\n" + object.getString(description);
        } else {
            if (object.has(status)) {
                int state = object.getInt(status);
                title = object.getString(wallbox.NAME) + "\n";
                switch (state) {
                    case 161:
                        title = title + "Ready.";
                        break;
                    case 179:
                        title = title + "Connected: waiting for next schedule.";
                        break;
                    case 181:
                        title = title + "Connected: waiting for car demand.";
                        break;
                    case 182:
                        title = title + "Paused by user.";
                        break;
                    case 194:
                        title = title + "Charging.";
                        break;
                    case 209:
                        title = title + "Locked.";
                        break;
                    case 210:
                        title = title + "Waiting for unlock.";
                        break;
                    default:
                        title = title + "Unknown state:" + state;
                        break;
                }
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
                wallbox.getWallboxState(chargerId);    // Start
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

                        handleStatus(state.getInt(wallbox.STATUS_ID), getTitle(state, wallbox.STATE_STATUS_DESCRIPTION, wallbox.STATUS_ID));

                        if (configData.getInt(wallbox.LOCKED) == 1) {  // LOCKED ?
                            radioGroupLocked.check(R.id.radioButtonLock);
                            croller.setBackCircleDisabledColor(getResources().getColor(R.color.locked));

                        } else if (configData.getInt(wallbox.LOCKED) == 0) {  // UNLOCKED ?
                            radioGroupLocked.check(R.id.radioButtonUnlock);
                            croller.setBackCircleDisabledColor(getResources().getColor(R.color.unlocked));

                        } else {
                            radioGroupLocked.clearCheck();
                            radioButtonUnLock.setTextColor(Color.RED);
                            radioButtonLock.setTextColor(Color.RED);
                        }
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
            LogThis.d(TAG, "wallboxLockChangeListener " + response);
            if (stateReceived) {
                try {
                    if (response.has(wallbox.DATA)) {
                        JSONObject data = response.getJSONObject(wallbox.DATA);
                        if (data.has(wallbox.CHARGER_DATA)) {
                            JSONObject chargerData = data.getJSONObject(wallbox.CHARGER_DATA);

                            handleStatus(chargerData.getInt(wallbox.STATUS), getTitle(chargerData, wallbox.STATUS_DESCRIPTION, wallbox.STATUS));

                            if (chargerData.getInt(wallbox.LOCKED) == 1) {
                                radioGroupLocked.check(R.id.radioButtonLock);
                                radioButtonLock.setTextColor(Color.GREEN);
                                croller.setBackCircleDisabledColor(getResources().getColor(R.color.locked));

                            } else if (chargerData.getInt(wallbox.LOCKED) == 0) {
                                radioGroupLocked.check(R.id.radioButtonUnlock);
                                radioButtonUnLock.setTextColor(Color.GREEN);
                                croller.setBackCircleDisabledColor(getResources().getColor(R.color.unlocked));

                            } else {
                                radioGroupLocked.clearCheck();
                                radioButtonUnLock.setTextColor(Color.RED);
                                radioButtonLock.setTextColor(Color.RED);
                            }
                            wallbox.getWallboxState(chargerId);  // Restart the timer
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
            LogThis.d(TAG, "wallboxActionChangeListener " + response);
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
                    wallbox.getWallboxState(chargerId);  // Restart the timer
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
            LogThis.d(TAG, "wallBoxMaxChargingCurrentListener " + response);
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
                    wallbox.getWallboxState(chargerId);  // Restart the timer
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
//            setRadioButtonsLockedUnlocked(false,Color.GRAY );
//            setRadioButtonsPauseResume(false,Color.GRAY );
        });

    }

    //---------------------------
}