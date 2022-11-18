package com.fullybelly.services.local.interfaces;

public interface DialogService {

    void showToast(String message);

    void showDialog(String text, String info, DialogLevel level);

    void showRequiredInfoDialog(String text, String info, boolean isHtml, boolean confirmationRequired);

    enum DialogLevel {
        STD,
        WARN
    }

}
