package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.IntroService;

public final class IntroServiceImpl extends BaseAppService implements IntroService {

    //region constructor

    public IntroServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public boolean userSawIntro() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        boolean sawIntro = preferences.getBoolean(getContext().getString(R.string.userSawIntroKey), false);

        return sawIntro;
    }

    @Override
    public void setUserSawIntro() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getContext().getString(R.string.userSawIntroKey), true);
        editor.apply();
    }

    //endregion

}
