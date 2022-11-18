package com.fullybelly.viewModels.fragments;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;

import com.fullybelly.R;
import com.fullybelly.events.offerDetail.PaymentStepFormRequested;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.services.results.ServiceResult;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

import javax.inject.Inject;

public final class PurchaseDetailsViewModel extends BasePurchaseViewModel {

    //region properties

    public ObservableField<String> amount = new ObservableField<>();

    public ObservableField<String> pickupFrom = new ObservableField<>();

    public ObservableField<String> pickupTo = new ObservableField<>();

    @Bindable
    public String getTotalPrice() {
        String amountValue = this.amount.get();
        int amount = !amountValue.equals("") ? Integer.parseInt(amountValue) : 0;
        double totalPrice = mUnitPrice * amount;
        DecimalFormat format = new DecimalFormat("0.00");

        return format.format(totalPrice) + mCurrency;
    }

    //endregion

    //region private fields

    private int mRestaurantId;

    private int mMealId;

    private int mMaxAmount;

    private double mUnitPrice;

    private String mCurrency;

    private final UserDetailsService mUserDetailsService;

    private final BookOrderService mBookOrderService;

    private OnPropertyChangedCallback mAmountChangedCallback;

    //endregion

    //region constructor

    @Inject
    public PurchaseDetailsViewModel(DialogService dialogService,
                                    TextService textService,
                                    UserDetailsService userDetailsService,
                                    PaymentMethodService paymentMethodService,
                                    OrdersSessionsService sessionsService,
                                    BookOrderService bookOrderService,
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

        if (mUserDetailsService.getEmailAddress() == null ||
                mUserDetailsService.getEmailAddress().equals("") ||
                !mPaymentMethodService.hasDefault()) {
            buttonText.set(mTextService.get(R.string.buttonNextStep));
        } else {
            buttonText.set(mTextService.get(R.string.buttonBuyText));
        }
    }

    //endregion

    //region public methods

    public void init(int restaurantId,
                     int mealId,
                     int maxAmount,
                     double unitPrice,
                     String currency,
                     String pickupFrom,
                     String pickupTo) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mMaxAmount = maxAmount;
        mUnitPrice = unitPrice;
        mCurrency = currency;

        this.pickupFrom.set(pickupFrom);
        this.pickupTo.set(pickupTo);
        amount.set("1");
        initEvents();
    }

    @Override
    public void sleep() {
        super.sleep();
        amount.removeOnPropertyChangedCallback(mAmountChangedCallback);

        mAmountChangedCallback = null;
    }

    public void increaseAmount() {
        int amountAsInt = Integer.parseInt(amount.get());

        amountAsInt++;

        amount.set(Integer.toString(amountAsInt));
    }

    public void decreaseAmount() {
        int amountAsInt = Integer.parseInt(amount.get());

        amountAsInt--;

        amount.set(Integer.toString(amountAsInt));
    }

    @Override
    public boolean canMakeNextStep() {
        String amountVal = amount.get();

        if (amountVal.equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void makeNextStep() {
        String savedEmail = mUserDetailsService.getEmailAddress();
        String amountVal = amount.get();

        int amountNum = Integer.valueOf(amountVal);
        int transactionValue = (int)((amountNum * mUnitPrice) * 100);

        if (savedEmail == null) {
            EventBus.getDefault().post(new PaymentStepFormRequested(
                    mRestaurantId,
                    mMealId,
                    transactionValue,
                    amountNum,
                    mCurrency,
                    PaymentStepFormRequested.Step.USER_DETAILS));
        } else if (!mPaymentMethodService.hasDefault()) {
            EventBus.getDefault().post(new PaymentStepFormRequested(
                    mRestaurantId,
                    mMealId,
                    transactionValue,
                    amountNum,
                    mCurrency,
                    PaymentStepFormRequested.Step.PAYMENT_METHOD));
        } else {
            buttonText.set(mTextService.get(R.string.loadingOngoing));
            mBookOrderService.configure(
                    mRestaurantId,
                    mMealId,
                    amountNum,
                    savedEmail,
                    this).run();
        }
    }

    //endregion

    //region private methods

    private void initEvents() {
        mAmountChangedCallback = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                String value = amount.get();

                try {
                    int number = Integer.parseInt(value);

                    if (number <= 0) {
                        amount.set("1");
                    } else if (number + 1 > mMaxAmount) {
                        amount.set(Integer.toString(mMaxAmount));
                    }

                } catch (NumberFormatException nfe) { }

                notifyChange();
            }
        };
        amount.addOnPropertyChangedCallback(mAmountChangedCallback);
    }

    @Override
    protected void makePayment(ServiceResult result) {
        if (!validateCanMakePayment()) {
            return;
        }

        int transactionValue = (int)((Integer.valueOf(amount.get()) * mUnitPrice) * 100);
        String email = mUserDetailsService.getEmailAddress();

        mPaymentServiceFactory
                .get()
                .makePayment(
                        transactionValue,
                        mCurrency,
                        mTextService.get(R.string.transferTitle),
                        email,
                        getPaymentType(),
                        mBookedOrder,
                        result);
    }

    //endregion

}
