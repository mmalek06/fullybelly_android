package com.fullybelly.services.local.implementations;

import android.app.Activity;
import android.util.Log;

import com.fullybelly.R;
import com.fullybelly.events.offerDetail.PaymentProcessingCompleted;
import com.fullybelly.events.offerDetail.PaymentProcessingStarted;
import com.fullybelly.events.offerDetail.PaymentResult;
import com.fullybelly.events.orders.OrderCheckRequested;
import com.fullybelly.models.api.Checkout;
import com.fullybelly.services.internet.orders.interfaces.CheckoutService;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentResultConsumer;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.CheckoutServiceResult;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

public final class PaypalPaymentResultConsumerImpl
        implements PaymentResultConsumer,
                   CheckoutService.ServiceCallback{

    //region fields

    private final TextService mTextService;

    private final OrdersNotificationService mOrdersNotificationService;

    private final DialogService mDialogService;

    private final CheckoutService mCheckoutService;

    private final BookedOrderCacheService mBookedOrderCacheService;

    //endregion

    //region constructor

    public PaypalPaymentResultConsumerImpl(TextService textService,
                                           OrdersNotificationService ordersNotificationService,
                                           DialogService dialogService,
                                           CheckoutService checkoutService,
                                           BookedOrderCacheService bookedOrderCacheService) {
        mTextService = textService;
        mOrdersNotificationService = ordersNotificationService;
        mDialogService = dialogService;
        mCheckoutService = checkoutService;
        mBookedOrderCacheService = bookedOrderCacheService;
    }

    //endregion

    //region public methods

    @Override
    public void consume(PaymentResult result) {
        EventBus.getDefault().post(new PaymentProcessingStarted());

        if (result.isSuccess()) {
            onSuccess(result);
        }
        else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            onCancel();
        }
        else if (result.getResultCode() == PaymentActivity.RESULT_EXTRAS_INVALID) {
            onFatalError();
        }
    }

    @Override
    public void gotCheckoutResult(CheckoutServiceResult result) {
        EventBus.getDefault().postSticky(new PaymentProcessingCompleted());

        Checkout checkout = result.getCheckout();

        if (checkout != null) {
            String message = mTextService.get(R.string.transactionSuccess);
            String info = mTextService.get(R.string.paymentOk);

            mDialogService.showDialog(message, info, DialogService.DialogLevel.STD);
            EventBus.getDefault().postSticky(new OrderCheckRequested(checkout.getSessionId()));
        }
    }

    @Override
    public void gotCheckoutError(CheckoutServiceResult result) {
        EventBus.getDefault().postSticky(new PaymentProcessingCompleted());

        Checkout checkout = result.getCheckout();

        if (checkout == null) {
            mDialogService.showDialog(
                    mTextService.get(R.string.genericError),
                    mTextService.get(R.string.paymentValidationReasurrance),
                    DialogService.DialogLevel.WARN);
        } else {
            for (String validation : result.getCheckout().getValidations()) {
                mDialogService.showDialog(mTextService.get(R.string.paymentValidationReasurrance), validation, DialogService.DialogLevel.WARN);
            }
        }
    }

    //endregion

    //region methods

    private void onSuccess(PaymentResult result) {
        PaymentConfirmation confirm = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

        if (confirm != null) {
            try {
                String paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
                String state = confirm.toJSONObject().getJSONObject("response").getString("state");
                String sessionId = mBookedOrderCacheService.pop().getSessionId();

                if (!state.toLowerCase().equals("approved")) {
                    String message = mTextService.get(R.string.transactionRejected);
                    String info = mTextService.get(R.string.transactionRejectedExplanation);

                    mDialogService.showDialog(message, info, DialogServiceImpl.DialogLevel.WARN);

                    return;
                }

                mCheckoutService.configure(this, paymentId, sessionId).run();
            }
            catch (JSONException e) {
                String message = mTextService.get(R.string.genericError);
                String info = mTextService.get(R.string.genericError);

                mDialogService.showDialog(message, info, DialogServiceImpl.DialogLevel.WARN);
            }
        }
    }

    private void onFatalError() {
        String message = mTextService.get(R.string.genericError);
        String info = mTextService.get(R.string.genericError);

        EventBus.getDefault().postSticky(new PaymentProcessingCompleted());
        mDialogService.showDialog(message, info, DialogServiceImpl.DialogLevel.WARN);
    }

    private void onCancel() {
        String message = mTextService.get(R.string.transactionAbandoned);
        String info = mTextService.get(R.string.paymentCancelled);

        EventBus.getDefault().postSticky(new PaymentProcessingCompleted());
        mDialogService.showDialog(message, info, DialogServiceImpl.DialogLevel.WARN);
    }

    //endregion

}
