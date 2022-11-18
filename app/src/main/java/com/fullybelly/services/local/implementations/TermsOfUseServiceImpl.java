package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.models.api.TermsOfUse;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.TermsOfUseService;

public final class TermsOfUseServiceImpl extends BaseAppService implements TermsOfUseService {

    //region constructor

    public TermsOfUseServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public int getLastIdentifier() {
        Context context = getContext();

        if (context == null) {
            return -1;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);

        return preferences.getInt(getContext().getString(R.string.settingsTermsAndConditionsId), 0);
    }

    @Override
    public void setTermsAndConditions(TermsOfUse termsOfUse) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getContext().getString(R.string.settingsTermsAndConditionsText), termsOfUse.getText());
        editor.putString(getContext().getString(R.string.settingsImpressumText), termsOfUse.getImpressum());
        editor.putInt(getContext().getString(R.string.settingsTermsAndConditionsId), termsOfUse.getId());
        editor.putBoolean(getContext().getString(R.string.settingsTermsAndConditionsAcceptedFlag), termsOfUse.isAccepted());
        editor.apply();
    }

    @Override
    public TermsOfUse getTermsOfUse() {
        Context context = getContext();

        if (context == null) {
            return new TermsOfUse(-1, "", "", false);
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        String text = preferences.getString(getContext().getString(R.string.settingsTermsAndConditionsText), null);
        String impressum = preferences.getString(getContext().getString(R.string.settingsImpressumText), null);
        int id = preferences.getInt(getContext().getString(R.string.settingsTermsAndConditionsId), 0);
        boolean isAccepted = preferences.getBoolean(getContext().getString(R.string.settingsTermsAndConditionsAcceptedFlag), false);

        return new TermsOfUse(id, text, impressum, isAccepted);
    }

    //endregion

}
