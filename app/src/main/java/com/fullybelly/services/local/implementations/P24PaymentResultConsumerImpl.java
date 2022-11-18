package com.fullybelly.services.local.implementations;

import com.fullybelly.R;
import com.fullybelly.events.offerDetail.PaymentResult;
import com.fullybelly.events.orders.OrderCheckRequested;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentResultConsumer;
import com.fullybelly.services.local.interfaces.TextService;

import org.greenrobot.eventbus.EventBus;

import pl.dialcom24.p24lib.P24;
import pl.dialcom24.p24lib.P24PaymentResult;

public final class P24PaymentResultConsumerImpl implements PaymentResultConsumer {

    //region fields

    private final TextService mTextService;

    private final OrdersNotificationService mOrdersNotificationService;

    private final OrdersSessionsService mOrdersSessionsService;

    private final DialogService mDialogService;

    //endregion

    //region constructor

    public P24PaymentResultConsumerImpl(TextService textService,
                                        OrdersNotificationService ordersNotificationService,
                                        OrdersSessionsService ordersSessionsService,
                                        DialogService dialogService) {
        mTextService = textService;
        mOrdersNotificationService = ordersNotificationService;
        mOrdersSessionsService = ordersSessionsService;
        mDialogService = dialogService;
    }

    //endregion

    //region public methods

    @Override
    public void consume(PaymentResult result) {
        String message;
        String info;
        boolean resultOk = false;

        if (result.isSuccess()) {
            P24PaymentResult payResult = P24.getPaymentResult(result.getData());
            boolean isPaymentOk = payResult.isOk();

            if (isPaymentOk) {
                message = mTextService.get(R.string.transactionSuccess);
                info = mTextService.get(R.string.paymentOk);
                resultOk = true;

                mOrdersNotificationService.markOrderPending();
                mOrdersNotificationService.markUnseen();
                EventBus.getDefault().postSticky(new OrderCheckRequested(payResult.getSessionId()));
            } else {
                message = mTextService.get(R.string.genericError);
                info = payResult.getStatusMessage();
            }
        } else {
            message = mTextService.get(R.string.transactionAbandoned);
            info = mTextService.get(R.string.paymentCancelled);

            EventBus.getDefault().postSticky(new OrderCheckRequested(mOrdersSessionsService.getLastSessionToCheck()));
        }

        mDialogService.showDialog(message, info, resultOk ? DialogServiceImpl.DialogLevel.STD : DialogServiceImpl.DialogLevel.WARN);
    }

    //endregion

}
