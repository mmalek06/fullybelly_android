package com.fullybelly.viewModels.mocks;

import com.fullybelly.services.local.interfaces.DialogService;

public final class DialogServiceMock implements DialogService {
    public enum DialogKind { NONE, TOAST, DIALOG, REQUIRED_INFO }
    public DialogKind currentDialog = DialogKind.NONE;

    @Override
    public void showToast(String message) {
        currentDialog = DialogKind.TOAST;
    }

    @Override
    public void showDialog(String text, String info, DialogLevel level) {
        currentDialog = DialogKind.DIALOG;
    }

    @Override
    public void showRequiredInfoDialog(String text, String info, boolean isHtml, boolean confirmationRequired) {
        currentDialog = DialogKind.REQUIRED_INFO;
    }
}
