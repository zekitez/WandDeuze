package com.sdsmdg.harjot.croller;

public interface OnCrollerChangeListener {
    void onProgressChanged(Croller croller, int progress);
    void onStartTrackingTouch(Croller croller);
    void onStopTrackingTouch(Croller croller);
}
