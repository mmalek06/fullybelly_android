package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.PaymentService;

public final class PaymentMethodServiceImpl extends BaseAppService implements PaymentMethodService {

    //region constructor

    public PaymentMethodServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public boolean isDefault(PaymentService.PaymentType paymentType) {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        boolean p24cc = preferences.getBoolean(getContext().getString(R.string.settingsPaymentMethodP24CC), false);
        boolean p24bt = preferences.getBoolean(getContext().getString(R.string.settingsPaymentMethodP24BT), false);

        switch (paymentType) {
            case CREDIT_CARD: return p24cc;
            case BANK_TRANSFER: return p24bt;
        }

        return false;
    }

    @Override
    public boolean hasDefault() {
        Context context = getContext();

        if (context == null) {
            return false;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);

        return preferences.contains(getContext().getString(R.string.settingsPaymentMethodP24CC)) ||
                preferences.contains(getContext().getString(R.string.settingsPaymentMethodP24BT));
    }

    @Override
    public void setDefault(PaymentService.PaymentType paymentType) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (paymentType) {
            case CREDIT_CARD:
                editor.putBoolean(getContext().getString(R.string.settingsPaymentMethodP24CC), true);
                editor.putBoolean(getContext().getString(R.string.settingsPaymentMethodP24BT), false);
                break;
            case BANK_TRANSFER:
                editor.putBoolean(getContext().getString(R.string.settingsPaymentMethodP24CC), false);
                editor.putBoolean(getContext().getString(R.string.settingsPaymentMethodP24BT), true);
                break;
        }

        editor.apply();
    }

    //endregion

}
