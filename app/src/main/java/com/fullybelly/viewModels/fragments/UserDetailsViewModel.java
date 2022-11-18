package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableField;

import com.fullybelly.R;
import com.fullybelly.events.offerDetail.PaymentStepFormRequested;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.services.results.ServiceResult;
import com.fullybelly.viewModels.validators.UserDetailsValidator;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public final class UserDetailsViewModel extends BasePurchaseViewModel {

    //region properties

    public ObservableField<String> emailAddress = new ObservableField<>();

    public ObservableField<String> emailValidation = new ObservableField<>();

    public UserDetailsValidator validator;

    //endregion

    //region fields

    private int mRestaurantId;

    private int mMealId;

    private int mAmount;

    private int mTransactionValue;

    private String mCurrency;

    private final UserDetailsService mUserDetailsService;

    private final BookOrderService mBookOrderService;

    //endregion

    //region constructor

    @Inject
    public UserDetailsViewModel(DialogService dialogService,
                                TextService textService,
                                UserDetailsService userDetailsService,
                                PaymentMethodService paymentMethodService,
                                OrdersSessionsService sessionsService,
                                BookOrderService bookOrderService,
                                UserDetailsValidator userDetailsValidator,
                                ConfigurationServiceFactory configurationServiceFactory,
                                PaymentServiceFactory paymentServiceFactory) {
        super(dialogService,
                textService,
                sessionsService,
                paymentMethodService,
                configurationServiceFactory,
                paymentServiceFactory);

        mUserDetailsService = userDetailsService;
        mBookOrderService = bookOrderService;
        validator = userDetailsValidator;

        initProperties();

        if (mPaymentMethodService.hasDefault()) {
            buttonText.set(mTextService.get(R.string.buttonBuyText));
        } else {
            buttonText.set(mTextService.get(R.string.buttonNextStep));
        }
    }

    //endregion

    //region public methods

    public void init(int restaurantId,
                     int mealId,
                     int amount,
                     int transactionValue,
                     String currency) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mAmount = amount;
        mTransactionValue = transactionValue;
        mCurrency = currency;
    }

    @Override
    public boolean canMakeNextStep() {
        if (!validator.validate(emailAddress.get())) {
            emailValidation.set(mTextService.get(R.string.emailInvalidValidation));

            return false;
        }

        return true;
    }

    @Override
    public void makeNextStep() {
        String email = emailAddress.get();

        emailValidation.set("");
        mUserDetailsService.setEmailAddress(email);

        if (mPaymentMethodService.hasDefault()) {
            buttonText.set(mTextService.get(R.string.loadingOngoing));
            mBookOrderService.configure(
                    mRestaurantId,
                    mMealId,
                    mAmount,
                    email,
                    this).run();
        } else {
            EventBus.getDefault().post(new PaymentStepFormRequested(
                    mRestaurantId,
                    mMealId,
                    mTransactionValue,
                    mAmount,
                    mCurrency,
                    PaymentStepFormRequested.Step.PAYMENT_METHOD));
        }
    }

    //endregion

    //region methods

    private void initProperties() {
        String email = mUserDetailsService.getEmailAddress();

        if (email != null) {
            emailAddress.set(email);
        }
    }

    @Override
    protected void makePayment(ServiceResult result) {
        if (!validateCanMakePayment()) {
            return;
        }

        String email = emailAddress.get();

        mPaymentServiceFactory
                .get()
                .makePayment(
                        mTransactionValue,
                        mCurrency,
                        mTextService.get(R.string.transferTitle),
                        email,
                        getPaymentType(),
                        mBookedOrder,
                        result);
    }

    //endregion

}
