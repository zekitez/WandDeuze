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

//        LogThis.d(TAG, "username           " + username);
//        LogThis.d(TAG, "password           " + password);
//        LogThis.d(TAG, "chargerId          " + chargerId);
        LogThis.d(TAG, "connection timeout " + connectionTimeOutSec);
    }

    //----------- GUI listeners

    // A listener per radioButton results in one call per button push.
    // The radioGroup onCheckGhangedListener triggers multiple calls per button push.

    final View.OnClickListener lockOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "lockOnClickListener");
            wallbox.setWallboxLock(chargerId, true);
        }
    };

    final View.OnClickListener unLockOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "un-LockOnClickListener");
            wallbox.setWallboxLock(chargerId, false);
        }
    };

    final View.OnClickListener pauzeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "pauzeOnClickListener");
            radioButtonResume.setEnabled(true);
            wallbox.setWallboxAction(chargerId,true);
        }
    };

    final View.OnClickListener resumeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogThis.d(TAG, "resumeOnClickListener");
            wallbox.setWallboxAction(chargerId,false);
        }
    };

    int progress = 0;
    final CompoundButton.OnCheckedChangeListener enableCurrentChangeListener = new CompoundButton.OnCheckedChangeListener() {
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

    private void setDescriptionPluggedIn(int status, String description){
        textViewMessage.setText(description);
        if (status == 161 || status == 209){  // 161 = ready, 209 = Locked
            checkBoxPluggedIn.setChecked(false);
        } else {
            checkBoxPluggedIn.setChecked(true);
        }
    }

    private String getTitle(JSONObject object, String description) throws JSONException {
        return (object.getString(wallbox.NAME) + "\n" + object.getString(description));
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
                setRadioButtonsLockedUnlocked(true,Color.WHITE );
                setRadioButtonsPauseResume(true,Color.WHITE );
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
//                        LogThis.d(TAG,"state max_available_power " + state.getString("max_available_power"));
                    } else {
                        croller.setMax(64);
                    }
                    if (state.has(wallbox.CONFIG_DATA)) {
                        JSONObject configData = state.getJSONObject(wallbox.CONFIG_DATA);
//                        LogThis.d(TAG, "state name = " + state.getString("name"));
//                        LogThis.d(TAG, "state status_description = " + state.getString("status_description"));
//                        LogThis.d(TAG, "configData locked = " + configData.getInt(wallbox.LOCKED));

                        //textViewMessage.setText(getTitle(state, wallbox.STATE_STATUS_DESCRIPTION));
                        setDescriptionPluggedIn(state.getInt(wallbox.STATUS_ID), getTitle(state, wallbox.STATE_STATUS_DESCRIPTION));

                        if (configData.getInt(wallbox.LOCKED) == 1) {
                            radioGroupLocked.check(R.id.radioButtonLock);
                            croller.setBackCircleDisabledColor(getResources().getColor(R.color.locked));
                            setRadioButtonsPauseResume(false,Color.GRAY );

                        } else if (configData.getInt(wallbox.LOCKED) == 0) {
                            radioGroupLocked.check(R.id.radioButtonUnlock);
                            croller.setBackCircleDisabledColor(getResources().getColor(R.color.unlocked));
                            setRadioButtonsPauseResume(true,Color.WHITE );

                        } else {
                            radioButtonUnLock.setTextColor(Color.RED);
                            radioButtonLock.setTextColor(Color.RED);
                        }
                        if (configData.has("remote_action")) {
                            if (configData.getInt("remote_action") == 1) {
                                radioGroupAction.check(R.id.radioButtonResume);
                            } else if (configData.getInt("remote_action") == 2) {
                                radioGroupAction.check(R.id.radioButtonPauze);
                            } else {
                                radioGroupAction.clearCheck();
                            }
                        } else {
                            radioGroupAction.clearCheck();
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
//                            textViewMessage.setText(getTitle(chargerData, wallbox.STATUS_DESCRIPTION));
                            setDescriptionPluggedIn(chargerData.getInt(wallbox.STATUS) ,getTitle(chargerData, wallbox.STATUS_DESCRIPTION));

                            if (chargerData.getInt(wallbox.LOCKED) == 1) {
                                radioGroupLocked.check(R.id.radioButtonLock);
                                radioButtonUnLock.setTextColor(Color.WHITE);
                                radioButtonLock.setTextColor(Color.GREEN);
                                croller.setBackCircleDisabledColor(getResources().getColor(R.color.locked));
                                setRadioButtonsPauseResume(false,Color.GRAY );

                            } else if (chargerData.getInt(wallbox.LOCKED) == 0) {
                                radioGroupLocked.check(R.id.radioButtonUnlock);
                                radioButtonUnLock.setTextColor(Color.GREEN);
                                radioButtonLock.setTextColor(Color.WHITE);
                                croller.setBackCircleDisabledColor(getResources().getColor(R.color.unlocked));
                                setRadioButtonsPauseResume(true,Color.WHITE );

                            } else {
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
                    if (response.has(wallbox.ACTION)) {
                        if (response.getInt(wallbox.ACTION) == 1){
                            radioButtonPauze.setTextColor(Color.WHITE);
                            radioButtonResume.setTextColor(Color.GREEN);
                        } else if (response.getInt(wallbox.ACTION) == 2){
                            radioButtonPauze.setTextColor(Color.GREEN);
                            radioButtonResume.setTextColor(Color.WHITE);
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
            setRadioButtonsLockedUnlocked(false,Color.GRAY );
            setRadioButtonsPauseResume(false,Color.GRAY );
        });

    }

    //---------------------------
