package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;

public final class OrdersNotificationServiceImpl extends BaseAppService implements OrdersNotificationService {

    //region constructor

    public OrdersNotificationServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public boolean userSawOrder() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);

        return preferences.getBoolean(getContext().getString(R.string.settingsOrderSeenFlag), false);
    }

    @Override
    public boolean isOrderPending() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);

        return preferences.getBoolean(getContext().getString(R.string.settingsOrderPendingFlag), false);
    }

    @Override
    public void markSeen() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getContext().getString(R.string.settingsOrderSeenFlag), true);
        editor.apply();
    }

    @Override
    public void markUnseen() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getContext().getString(R.string.settingsOrderSeenFlag), false);
        editor.apply();
    }

    @Override
    public void markOrderPending() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getContext().getString(R.string.settingsOrderPendingFlag), true);
        editor.apply();
    }

    @Override
    public void removeOrderPending() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getContext().getString(R.string.settingsOrderPendingFlag), false);
        editor.apply();
    }

    //endregion

}
