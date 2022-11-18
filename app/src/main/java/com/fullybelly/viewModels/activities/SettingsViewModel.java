package com.fullybelly.viewModels.activities;

import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.fullybelly.R;
import com.fullybelly.services.local.interfaces.NotificationService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.viewModels.base.BaseViewModel;
import com.fullybelly.viewModels.validators.PaymentMethodValidator;
import com.fullybelly.viewModels.validators.UserDetailsValidator;

import javax.inject.Inject;

public final class SettingsViewModel extends BaseViewModel {

    //region properties

    public ObservableField<String> emailAddress = new ObservableField<>();

    public ObservableField<String> emailValidation = new ObservableField<>();

    @Bindable
    public boolean getP24CreditCardSelected() { return mPaymentMethodService.isDefault(PaymentService.PaymentType.CREDIT_CARD) ? true : false; }
    public void setP24CreditCardSelected(boolean value) {
        if (value) {
            mPaymentMethodService.setDefault(PaymentService.PaymentType.CREDIT_CARD);
        }
    }

    @Bindable
    public boolean getP24BankTransferSelected() { return mPaymentMethodService.isDefault(PaymentService.PaymentType.BANK_TRANSFER) ? true : false; }
    public void setP24BankTransferSelected(boolean value) {
        if (value) {
            mPaymentMethodService.setDefault(PaymentService.PaymentType.BANK_TRANSFER);
        }
    }

    //endregion

    //region fields

    private final TextService mTextService;

    private final UserDetailsService mUserDetailsService;

    private final PaymentMethodService mPaymentMethodService;

    private final NotificationService mNotificationService;

    private final UserDetailsValidator mUserDetailsValidator;

    //endregion

    //region constructor

    @Inject
    public SettingsViewModel(TextService textService,
                             UserDetailsService userDetailsService,
                             PaymentMethodService paymentMethodService,
                             NotificationService notificationService,
                             UserDetailsValidator userDetailsValidator) {
        mTextService = textService;
        mUserDetailsService = userDetailsService;
        mPaymentMethodService = paymentMethodService;
        mNotificationService = notificationService;
        mUserDetailsValidator = userDetailsValidator;

        initProperties();
    }

    //endregion

    //region public methods

    public void save() {
        if (!mUserDetailsValidator.validate(emailAddress.get())) {
            emailValidation.set(mTextService.get(R.string.emailInvalidValidation));

            return;
        }

        emailValidation.set("");
        mUserDetailsService.setEmailAddress(emailAddress.get());
        mNotificationService.showNotification(mTextService.get(R.string.settingsSaved));
    }

    //endregion

    //region methods

    private void initProperties() {
        String email = mUserDetailsService.getEmailAddress();

        emailAddress.set(email);
    }

    //endregion

}
