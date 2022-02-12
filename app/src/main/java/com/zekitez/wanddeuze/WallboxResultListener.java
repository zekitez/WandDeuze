package com.zekitez.wanddeuze;

import org.json.JSONObject;

public interface WallboxResultListener {
    void wallboxConnectedListener(boolean connected, String text);
    void wallboxStateListener(boolean stateReceived, JSONObject state);
    void wallboxLockChangeListener(boolean stateReceived, JSONObject response);
    void wallboxActionChangeListener(boolean stateReceived, JSONObject response);
    void wallboxMaxChargingCurrentListener(boolean stateReceived, JSONObject response);
    void wallboxErrorListener(String error);
}
