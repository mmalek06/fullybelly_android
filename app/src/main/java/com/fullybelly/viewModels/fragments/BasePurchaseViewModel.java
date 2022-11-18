package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.fullybelly.R;
import com.fullybelly.events.offerDetail.DetailsLoadCompleted;
import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.services.internet.orders.interfaces.BookOrderService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationService;
import com.fullybelly.services.internet.orders.interfaces.ConfigurationServiceFactory;
import com.fullybelly.services.local.interfaces.PaymentServiceFactory;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentMethodService;
import com.fullybelly.services.local.interfaces.PaymentService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OrdersServiceResult;
import com.fullybelly.services.results.ServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.base.BaseViewModel;

import org.greenrobot.eventbus.EventBus;

public abstract class BasePurchaseViewModel extends BaseViewModel implements
        BookOrderService.ServiceCallback,
        ConfigurationService.ServiceCallback {

    //region fields

    protected final DialogService mDialogService;

    protected final TextService mTextService;

    protected final OrdersSessionsService mSessionsService;

    protected final PaymentMethodService mPaymentMethodService;

    protected final ConfigurationServiceFactory mConfigurationServiceFactory;

    protected final PaymentServiceFactory mPaymentServiceFactory;

    protected BookedOrder mBookedOrder;

    protected boolean mShouldAutoDismissDialog;

    //endregion

    //region properties

    public ObservableField<String> buttonText = new ObservableField<>();

    public ObservableBoolean dialogDismissRequested = new ObservableBoolean();

    //endregion

    //region constructor

    public BasePurchaseViewModel(
            DialogService dialogService,
            TextService textService,
            OrdersSessionsService sessionsService,
            PaymentMethodService paymentMethodService,
            ConfigurationServiceFactory configurationServiceFactory,
            PaymentServiceFactory paymentServiceFactory) {
        mDialogService = dialogService;
        mTextService = textService;
        mSessionsService = sessionsService;
        mPaymentMethodService = paymentMethodService;
        mConfigurationServiceFactory = configurationServiceFactory;
        mPaymentServiceFactory = paymentServiceFactory;
    }

    //endregion

    //region public methods

    @Override
    public void gotBookingResult(OrdersServiceResult result) {
        mSessionsService.persistSession(result.getOrder().getSessionId());
        onOrderBooked(result.getOrder());
    }

    @Override
    public void gotConfigurationResult(ServiceResult result) { makePayment(result); }

    @Override
    public void gotBookingError(OrdersServiceResult result) { handleError(result); }

    @Override
    public void gotConfigurationError(ServiceResult result) { handleError(result); }

    public abstract void makeNextStep();

    public abstract boolean canMakeNextStep();

    //endregion

    //region internal methods

    protected boolean validateCanMakePayment() {
        if (!mPaymentServiceFactory.get().canMakePayment()) {
            mDialogService.showDialog(
                    mTextService.get(R.string.paymentPreparationError),
                    mTextService.get(R.string.paymentPreparationErrorDescription),
                    DialogService.DialogLevel.WARN);

            return false;
        }

        return true;
    }

    protected PaymentService.PaymentType getPaymentType() {
        boolean creditCard = mPaymentMethodService.isDefault(PaymentService.PaymentType.CREDIT_CARD);
        boolean bankTransfer = mPaymentMethodService.isDefault(PaymentService.PaymentType.BANK_TRANSFER);
        boolean paypal = mPaymentMethodService.isDefault(PaymentService.PaymentType.PAYPAL);

        if (creditCard) {
            return PaymentService.PaymentType.CREDIT_CARD;
        }
        if (bankTransfer) {
            return PaymentService.PaymentType.BANK_TRANSFER;
        }
        if (paypal) {
            return PaymentService.PaymentType.PAYPAL;
        }

        return PaymentService.PaymentType.NONE;
    }

    protected void onOrderBooked(BookedOrder bookedOrder) {
        if (!validateCanMakePayment()) {
            return;
        }

        mBookedOrder = bookedOrder;

        mConfigurationServiceFactory.get().configure(this).run();
    }

    protected abstract void makePayment(ServiceResult result);

    private void handleError(ServiceResult result) {
        ServiceResultCodes code = result.getResultCode();

        switch (code) {
            case NO_DATA:
                mDialogService.showDialog(
                        mTextService.get(R.string.noOfferInfo),
                        mTextService.get(R.string.noOfferExplanation),
                        DialogService.DialogLevel.WARN);
                break;
            case BAD_DATA_RECEIVED:
            case WRONG_ARGUMENT:
            case IMPROPERLY_CONFIGURED:
                mDialogService.showDialog(
                        mTextService.get(R.string.orderBookingFailed),
                        mTextService.get(R.string.tryAgainLater),
                        DialogService.DialogLevel.WARN);
                break;
            case NO_PERMISSIONS:
                mDialogService.showDialog(
                        mTextService.get(R.string.permissionsDenied),
                        mTextService.get(R.string.addPermissionsAndTryAgain),
                        DialogService.DialogLevel.WARN);
                break;
            case NO_PROVIDER_AVAILABLE:
                mDialogService.showDialog(
                        mTextService.get(R.string.providerUnavailable),
                        "",
                        DialogService.DialogLevel.WARN);
                break;
            case VALIDATION_ERROR:
                mDialogService.showDialog(
                        mTextService.get(R.string.orderBookingFailed),
                        result.getError(),
                        DialogService.DialogLevel.WARN);
                EventBus.getDefault().post(new DetailsLoadCompleted(null));
                break;
        }
    }

    //endregion

}
