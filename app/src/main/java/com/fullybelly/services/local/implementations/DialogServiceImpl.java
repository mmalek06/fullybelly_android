package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.views.fragments.common.NotificationFragment;
import com.fullybelly.views.fragments.common.NotificationRequiredFragment;

public final class DialogServiceImpl extends BaseAppService implements DialogService {

    //region constructor

    public DialogServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void showToast(String message) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog(String text, String info, DialogLevel level) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        DialogFragment newFragment = NotificationFragment.newInstance(text, info, level);
        newFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "notification");
    }

    @Override
    public void showRequiredInfoDialog(String text, String info, boolean isHtml, boolean confirmationRequired) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        DialogFragment newFragment = NotificationRequiredFragment.newInstance(text, info, isHtml, confirmationRequired);
        newFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "notification-required");
    }

    //endregion

}
