package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.UserDetailsService;

public final class UserDetailsServiceImpl extends BaseAppService implements UserDetailsService {

    //region constructor

    public UserDetailsServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public String getEmailAddress() {
        Context context = getContext();

        if (context == null) {
            return "";
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);

        return preferences.getString(getContext().getString(R.string.settingsEmailAddress), null);
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getContext().getString(R.string.settingsEmailAddress), emailAddress);
        editor.apply();
    }

    //endregion

}
