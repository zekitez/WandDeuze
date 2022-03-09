package com.zekitez.wanddeuze;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class WallboxPulsarPlus {
    final String TAG = "WallboxPulsarPlus";

    final String DATA = "data";
    final String CONFIG_DATA = "config_data";
    final String CHARGER_DATA = "chargerData";
    final String NAME = "name";
    final String STATUS_DESCRIPTION = "statusDescription";
    final String STATE_STATUS_DESCRIPTION = "status_description";
    final String STATUS_ID = "status_id";
    final String STATUS = "status";
    final String LOCKED = "locked";
    final String ACTION = "action";

    final String RESPONSECODE  = ".ResponseCode: ";
    final String RESPONSE = ".Response: ";
    final String MSG = " msg: ";

    final String URL_DOMAIN = "https://api.wall-box.com/";
    final String URL_AUTHENTICATION = "auth/token/user";
    final String URL_V2_CHARGER = "v2/charger/";
    final String URL_V3_CHARGERS = "v3/chargers/";
    final String URL_REMOTE_ACTION = "/remote-action";
    final String URL_STATUS = "chargers/status/";

    final String METHOD_GET = "GET";
    final String METHOD_PUT = "PUT";
    final String METHOD_POST = "POST";

    final String ERROR_USER_PWD = "Username, password, charger Id or not Online ??\n";

    private final WallboxResultListener callback;

    private Timer timer = null;

    private int timeOutMs = 2000;
    private String encodedUserPassword = null, token = null;

    public WallboxPulsarPlus(WallboxResultListener callback) {
        this.callback = callback;
    }

    public void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // Example connectToWallbox: getStringResponse
    //        {"jwt":"eye_some_very_long_code_which_I_shortened", "user_id":123456, "ttl":0123456789, "error":false, "status":200}
    private final String CONNECTTOWALLBOX = "connectToWallbox";

    public void connectToWallbox(String user, String password, int timeOutSec) {
        this.timeOutMs = Math.abs(timeOutSec * 1000);   // Millieseconds
        new Thread(() -> {
            HttpsURLConnection connection = null;
            try {
                LogThis.d(TAG, CONNECTTOWALLBOX + " timeOutSeconds:" + timeOutSec);
                int responseCode;
                String userPassword = user + ":" + password;
                byte[] userPasswordInBytes = userPassword.getBytes(StandardCharsets.UTF_8);
                encodedUserPassword = Base64.encodeToString(userPasswordInBytes, Base64.DEFAULT);

                URL url = new URL(URL_DOMAIN + URL_AUTHENTICATION);
                connection = (HttpsURLConnection) url.openConnection();

                connection.setRequestMethod(METHOD_GET);
                connection.setRequestProperty("Authorization", "Basic " + encodedUserPassword);
                connection = setCommonProperties(connection);

                responseCode = connection.getResponseCode();
                String text = connection.getResponseMessage();
                LogThis.d(TAG, CONNECTTOWALLBOX + RESPONSECODE + responseCode + MSG + text);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject response = getJsonResponse(connection);
                    LogThis.d(TAG, CONNECTTOWALLBOX + RESPONSE + response);
                    token = response.getString("jwt");
                    callback.wallboxConnectedListener(true, text);
                } else {
                    callback.wallboxErrorListener(ERROR_USER_PWD + text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String text = CONNECTTOWALLBOX + " " + e.getMessage();
                LogThis.e(TAG, text);
                callback.wallboxErrorListener(CONNECTTOWALLBOX + " " + e.getMessage() + ".  No internet ?");
            } finally {
                connection.disconnect();
            }
        }).start();
    }

    // Example getWallboxState response:
// {
//    "user_id":123456,               "user_name":"Captain",         "car_id":1,
//    "car_plate":"",                 "depot_price":0.2,             "last_sync":"2022-02-02 10:47:01",
//    "power_sharing_status":256,     "mid_status":0,                "status_id":209,
//    "status_description":"Locked",  "name":"PulsarPlus SN 12345",  "charging_power":0,
//    "max_available_power":20,       "depot_name":"Family",         "charging_speed":0,
//    "added_range":92,               "added_energy":11.094,         "added_green_energy":0,
//    "added_discharged_energy":0,    "charging_time":345997,        "cost":0,
//    "current_mode":3,               "preventive_discharge":false,  "state_of_charge":null,
//    "ocpp_status":1,                "config_data":{
//                                                   "charger_id":12345,             "uid":"ABCDEFGHIJKLMNOPQRSTUVWXYZ",
//                                                   "serial_number":"12345",        "name":"PulsarPlus SN 12345",
//                                                    "locked":1,                    "auto_lock":0,
//                                                    "auto_lock_time":60,           "multiuser":0,
//                                                    "max_charging_current":16,     "language":"EN",
//                                                    "icp_max_current":16,          "grid_type":1,
//                                                    "energy_price":0.2,            "energyCost":{ "value":0.2, "inheritedGroupId":123456 },
//                                                    "unlock_user_id":null,         "power_sharing_config":256,
//                                                    "purchased_power":0,           "show_name":1,
//                                                    "show_lastname":1,             "show_email":1,
//                                                    "show_profile":1,              "show_default_user":1,
//                                                    "gesture_status":7,            "home_sharing":1,
//                                                    "dca_status":0,                "connection_type":1,
//                                                    "max_available_current":20,    "live_refresh_time":30,
//                                                    "update_refresh_time":300,     "owner_id":123456,
//                                                    "remote_action":0,             "rfid_type":null,
//                                                    "charger_has_image":0,         "sha256_charger_image":null,
//                                                    "plan":{"plan_name":"Basic",   "features":["DEFAULT_FEATURE","POWER_BOOST","MOBILE_CONNECTIVITY","AUTOMATIC_REPORTING","STATISTICS"]},
//                                                    "sync_timestamp":1643525710,   "currency":{"id":1, "name":"Euro Member Countries", "symbol":"€", "code":"EUR"},
//                                                    "charger_load_type":"Private", "contract_charging_available":false,
//                                                    "country":{"id":166,"code":"NLD","iso2":"NL","name":"HOLANDA","phone_code":"31"},
//                                                    "state":null,                   "part_number":"PLP1-0-1-2-3-004-C",
//                                                    "software":{"updateAvailable":false, "currentVersion":"5.5.10", "latestVersion":"5.5.10"},
//                                                    "available":1,                  "operation_mode":"wallbox",
//                                                    "ocpp_ready":"ocpp_1.6j",       "tariffs":[],
//                                                    "mid_enabled":0,                "mid_margin":1,
//                                                    "mid_margin_unit":1,            "mid_serial_number":"",
//                                                    "mid_status":0,                 "session_segment_length":0,
//                                                    "group_id":123456
//                                                   }
// }
    private String chargerId;

    public void getWallboxState(String chargerId) {
        if (token == null) return;
        this.chargerId = chargerId;
        destroyTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new periodicStateRequest(), 1000, 5000);
    }

    private final String PERIODICSTATEREQUEST = "periodicStateRequest";

    class periodicStateRequest extends TimerTask {
        public void run() {
            HttpsURLConnection connection = null;
            try {
                LogThis.d(TAG, PERIODICSTATEREQUEST);
                int responseCode;
                URL url = new URL(URL_DOMAIN + URL_STATUS + chargerId);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod(METHOD_GET);
                connection = setAuthorisation(connection);
                connection = setCommonProperties(connection);

                responseCode = connection.getResponseCode();
                String text = connection.getResponseMessage();
                LogThis.d(TAG, PERIODICSTATEREQUEST + RESPONSECODE + responseCode + MSG + text);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject response = getJsonResponse(connection);
                    LogThis.d(TAG, PERIODICSTATEREQUEST + RESPONSE + response);
                    callback.wallboxStateListener(true, response);
                } else {
                    callback.wallboxErrorListener(PERIODICSTATEREQUEST + " " + text + "\n" + ERROR_USER_PWD);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String text = PERIODICSTATEREQUEST + " " + e.getMessage();
                LogThis.e(TAG, text);
                callback.wallboxErrorListener(text);
            } finally {
                connection.disconnect();
            }
        }
    }

    // Example setWallboxLock: getJsonResponse
    //  {"data":
    //     {"chargerData":
    //        {"id":12345,                   "uid":"ABCDEFGHIJKLMNOPQRSTUVWXYZ",                 "serialNumber":"12345",
    //         "name":"PulsarPlus SN 12345", "group":123123,                                     "chargerType":"PulsarPlus",
    //         "softwareVersion":"5.5.10",   "status":209,                                       "statusDescription":"Locked",
    //         "ocppConnectionStatus":1,     "ocppReady":"ocpp_1.6j",                            "stateOfCharge":null,
    //         "maxChgCurrent":15,           "maxAvailableCurrent":20,                           "maxChargingCurrent":16,
    //         "locked":1,                   "lastConnection":1643760501,                        "lastSync":{"date":"2022-01-29 14:46:40.000000","timezone_type":3,"timezone":"UTC"},
    //         "midEnabled":0,               "midMargin":1,                                      "midMarginUnit":1,
    //         "midSerialNumber":"",         "midStatus":0,                                      "wifiSignal":50,
    //         "connectionType":"wifi",      "chargerLoadName":"Private",                        "chargerLoadId":2,
    //         "chargingType":"AC",          "connectorType":"Type 2\/Socket",                   "protocolCommunication":"wifi",
    //         "accessType":"guest",         "powerSharingStatus":0,
    //         "resume":{
    //              "totalUsers":1,          "totalSessions":0,            "chargingTime":null,
    //              "totalEnergy":0,         "totalMidEnergy":0,           "energyUnit":"kWh"}
    //        },
    //      "users":[{"id":123456,"avatar":null,"name":"Captain","surname":"Kirk","email":"enterprice@gmail.com","profile":"super-admin","assigned":true,"createdByUser":false}]
    //     }
    //  }
    private final String SETWALLBOXLOCK = "setWallboxLock";

    public void setWallboxLock(String chargerId, boolean locked) {
        if (token == null) return;
        new Thread(() -> {
            HttpsURLConnection connection = null;
            try {
                int responseCode;
                LogThis.d(TAG, SETWALLBOXLOCK + " locked:" + locked);
                URL url = new URL(URL_DOMAIN + URL_V2_CHARGER + chargerId);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod(METHOD_PUT);
                connection = setAuthorisation(connection);
                connection = setCommonProperties(connection);

                JSONObject control = new JSONObject();
                control.put(LOCKED, (locked ? 1 : 0));
                writeData(connection, control.toString());

                responseCode = connection.getResponseCode();
                String text = connection.getResponseMessage();
                LogThis.d(TAG, SETWALLBOXLOCK + RESPONSECODE + responseCode + MSG + text);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject response = getJsonResponse(connection);
                    LogThis.d(TAG, SETWALLBOXLOCK + RESPONSE + response);
                    callback.wallboxLockChangeListener(true, response);
                } else {
                    callback.wallboxErrorListener(SETWALLBOXLOCK + " " + text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String text = SETWALLBOXLOCK + " " + e.getMessage();
                LogThis.e(TAG, text);
                callback.wallboxErrorListener(text);
            } finally {
                connection.disconnect();
            }
        }).start();
    }

    private final String SETWALLBOXACTION = "setWallboxAction";

    public void setWallboxAction(String chargerId, boolean actionPauze) {
        if (token == null) return;
        new Thread(() -> {
            HttpsURLConnection connection = null;
            try {
                int responseCode;
                LogThis.d(TAG, SETWALLBOXACTION + " pauze:" + actionPauze);

                URL url = new URL(URL_DOMAIN + URL_V3_CHARGERS + chargerId + URL_REMOTE_ACTION);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod(METHOD_POST);
                connection = setAuthorisation(connection);
                connection = setCommonProperties(connection);

                JSONObject control = new JSONObject();
                control.put(ACTION, (actionPauze ? 2 : 1));  // Pause
                writeData(connection, control.toString());

                responseCode = connection.getResponseCode();
                String text = connection.getResponseMessage();
                LogThis.d(TAG, SETWALLBOXACTION + RESPONSECODE + responseCode + MSG + text);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject response = getJsonResponse(connection);
                    LogThis.d(TAG, SETWALLBOXACTION + RESPONSE + response);
                    callback.wallboxActionChangeListener(true, response);
                } else {
                    callback.wallboxErrorListener(SETWALLBOXACTION + " " + text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String text = e.getMessage();
                LogThis.e(TAG, SETWALLBOXACTION + " " + e.getMessage());
                try {
                    callback.wallboxActionChangeListener(false, new JSONObject(text));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            } finally {
                connection.disconnect();
            }
        }).start();
    }

    private final String SETWALLBOXMAXCHARGINGCURRENT = "setWallboxMaxChargingCurrent";

    public void setWallboxMaxChargingCurrent(String chargerId, int maxChargingCurrent) {
        if (token == null) return;
        new Thread(() -> {
            HttpsURLConnection connection = null;
            try {
                int responseCode;
                LogThis.d(TAG, SETWALLBOXMAXCHARGINGCURRENT + " current:" + maxChargingCurrent);

                URL url = new URL(URL_DOMAIN + URL_V2_CHARGER + chargerId);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod(METHOD_PUT);
                connection = setAuthorisation(connection);
                connection = setCommonProperties(connection);

                JSONObject control = new JSONObject();
                control.put("maxChargingCurrent", maxChargingCurrent);
                writeData(connection, control.toString());

                responseCode = connection.getResponseCode();
                String text = connection.getResponseMessage();
                LogThis.d(TAG, "ResponseCode: " + responseCode + MSG + text);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject response = getJsonResponse(connection);
                    LogThis.d(TAG, SETWALLBOXMAXCHARGINGCURRENT + " response: " + response);
                    callback.wallboxMaxChargingCurrentListener(true, response);
                } else {
                    callback.wallboxErrorListener(SETWALLBOXMAXCHARGINGCURRENT + " " + text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String text = SETWALLBOXMAXCHARGINGCURRENT + " " + e.getMessage();
                LogThis.e(TAG, text);
                callback.wallboxErrorListener(text);
            } finally {
                connection.disconnect();
            }
        }).start();
    }


    //---------------------------------
    
    private HttpsURLConnection setCommonProperties(HttpsURLConnection connection) {
        if (connection != null) {
            connection.setConnectTimeout(timeOutMs);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        }
        return connection;
    }

    private HttpsURLConnection setAuthorisation(HttpsURLConnection connection) {
        if (connection != null && token != null) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        return connection;
    }

    private String getStringResponse(HttpsURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        for (String line; (line = br.readLine()) != null; ) response.append(line + "\n");
        return response.toString();
    }

    private JSONObject getJsonResponse(HttpsURLConnection connection) throws JSONException, IOException {
        return new JSONObject(getStringResponse(connection));
    }

    private void writeData(HttpsURLConnection connection, String query) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(query);
        writer.close();
    }

}
