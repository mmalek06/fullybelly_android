package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.os.Bundle;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.events.offerDetail.PaymentResult;
import com.fullybelly.services.internet.orders.interfaces.CheckoutService;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.PaymentResultConsumer;
import com.fullybelly.services.local.interfaces.PaymentResultConsumerFactory;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.util.MetadataHelper;

import javax.inject.Inject;

public final class PaymentResultConsumerFactoryImpl implements PaymentResultConsumerFactory {

    //region constants

    //region constants

    private final static String P24_REQ_CODE = "com.fullybelly.P24_REQ_CODE";

    private final static String PAYPAL_REQ_CODE = "com.fullybelly.PAYPAL_REQ_CODE";

    //endregion

    //endregion

    //region fields

    private final FullyBellyApplication mApplication;

    private final TextService mTextService;

    private final OrdersNotificationService mOrdersNotificationService;

    private final OrdersSessionsService mOrdersSessionsService;

    private final DialogService mDialogService;

    private final BookedOrderCacheService mBookedOrderCacheService;

    private final CheckoutService mCheckoutService;

    //endregion

    //region constructor

    @Inject
    public PaymentResultConsumerFactoryImpl(FullyBellyApplication application,
                                            TextService textService,
                                            OrdersNotificationService ordersNotificationService,
                                            OrdersSessionsService ordersSessionsService,
                                            DialogService dialogService,
                                            CheckoutService checkoutService,
                                            BookedOrderCacheService bookedOrderCacheService) {
        mApplication = application;
        mTextService = textService;
        mOrdersNotificationService = ordersNotificationService;
        mOrdersSessionsService = ordersSessionsService;
        mDialogService = dialogService;
        mCheckoutService = checkoutService;
        mBookedOrderCacheService = bookedOrderCacheService;
    }

    //endregion

    //region public methods

    @Override
    public PaymentResultConsumer get(PaymentResult result) {
        Context ctx = mApplication.getCurrentActivity();
        Bundle metadata = MetadataHelper.getMetadata(ctx);
        int p24code = metadata.getInt(P24_REQ_CODE);
        int ppcode = metadata.getInt(PAYPAL_REQ_CODE);

        if (p24code == result.getRequestCode()) {
            return new P24PaymentResultConsumerImpl(mTextService,
                                                    mOrdersNotificationService,
                                                    mOrdersSessionsService,
                                                    mDialogService);
        }
        if (ppcode == result.getRequestCode()) {
            return new PaypalPaymentResultConsumerImpl(mTextService,
                                                       mOrdersNotificationService,
                                                       mDialogService,
                                                       mCheckoutService,
                                                       mBookedOrderCacheService);
        }

        return null;
    }

    //endregion

}
