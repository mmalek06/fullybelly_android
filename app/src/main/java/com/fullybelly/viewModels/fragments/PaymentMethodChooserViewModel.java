package com.fullybelly.viewModels.fragments;

import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.fullybelly.R;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.services.results.ServiceResult;
import com.fullybelly.viewModels.validators.PaymentMethodValidator;

import javax.inject.Inject;

public final class PaymentMethodChooserViewModel extends BasePurchaseViewModel {

    //region private fields

    private final UserDetailsService mUserDetailsService;

    private final BookOrderService mBookOrderService;

    private final PaymentMethodValidator mPaymentMethodValidator;

    private int mRestaurantId;

    private int mMealId;

    private int mAmount;

    private int mTransactionValue;

    private String mCurrency;

    private PaymentService.PaymentType mPaymentType;

    private boolean mCreditCardSelected;

    private boolean mBankTransferSelected;

    //endregion

    //region properties

    @Bindable
    public boolean getCreditCardSelected() { return mCreditCardSelected; }
    public void setCreditCardSelected(boolean value) {
        mCreditCardSelected = value;
        mPaymentType = PaymentService.PaymentType.CREDIT_CARD;
    }

    @Bindable
    public boolean getBankTransferSelected() { return mBankTransferSelected; }
    public void setBankTransferSelected(boolean value) {
        mBankTransferSelected = value;
        mPaymentType = PaymentService.PaymentType.BANK_TRANSFER;
    }

    public ObservableField<String> paymentMethodValidation = new ObservableField<>();

    public ObservableBoolean paymentMethodValid = new ObservableBoolean();

    public ObservableBoolean rememberChoice = new ObservableBoolean();

    public ObservableBoolean showDialogContents = new ObservableBoolean();

    //endregion

    //region constructor

    @Inject
    public PaymentMethodChooserViewModel(DialogService dialogService,
                                         TextService textService,
                                         UserDetailsService userDetailsService,
                                         PaymentMethodService paymentMethodService,
                                         OrdersSessionsService sessionsService,
                                         BookOrderService bookOrderService,
                                         PaymentMethodValidator paymentMethodValidator,
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
        mPaymentMethodValidator = paymentMethodValidator;
        mPaymentType = PaymentService.PaymentType.NONE;

        paymentMethodValid.set(true);

        if (mUserDetailsService.getEmailAddress() == null || mUserDetailsService.getEmailAddress().equals("")) {
            buttonText.set(mTextService.get(R.string.buttonNextStep));
        } else {
            buttonText.set(mTextService.get(R.string.buttonBuyText));
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

        if (!mPaymentServiceFactory.get().shouldInitializeWithPaymentMethod()) {
            mShouldAutoDismissDialog = true;

            makeNextStep();
        } else {
            showDialogContents.set(true);
        }
    }

    @Override
    public boolean canMakeNextStep() {
        if (!validateCanMakePayment()) {
            return false;
        }
        if (!mPaymentMethodValidator.validate(mPaymentType)) {
            paymentMethodValid.set(false);
            paymentMethodValidation.set(mTextService.get(R.string.paymentMethodRequired));

            return false;
        }

        return true;
    }

    @Override
    public void makeNextStep() {
        String email = mUserDetailsService.getEmailAddress();

        paymentMethodValid.set(true);
        buttonText.set(mTextService.get(R.string.loadingOngoing));
        mBookOrderService.configure(
                mRestaurantId,
                mMealId,
                mAmount,
                email,
                this).run();
    }

    //endregion

    //region internal methods

    @Override
    protected void makePayment(ServiceResult result) {
        if (!validateCanMakePayment()) {
            return;
        }

        String email = mUserDetailsService.getEmailAddress();
        PaymentService.PaymentType paymentType = mCreditCardSelected ?
                PaymentService.PaymentType.CREDIT_CARD :
                PaymentService.PaymentType.BANK_TRANSFER;

        if (rememberChoice.get()) {
            mPaymentMethodService.setDefault(paymentType);
        }

        mPaymentServiceFactory
                .get()
                .makePayment(
                        mTransactionValue,
                        mCurrency,
                        mTextService.get(R.string.transferTitle),
                        email,
                        paymentType,
                        mBookedOrder,
                        result);
        dialogDismissRequested.set(true);
    }

    //endregion

}
