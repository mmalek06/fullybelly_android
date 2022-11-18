package com.fullybelly.services.background;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.models.api.FreshOrderInfo;
import com.fullybelly.services.results.OrdersMonitorServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public final class OrderMonitorService extends IntentService {

    //region constants

    public static final String SESSION_ID_KEY = "session-id";

    //endregion

    //region public fields

    @Inject
    public com.fullybelly.services.internet.orders.interfaces.OrderMonitorService monitorService;

    //endregion

    //region constructor

    public OrderMonitorService() { super("OrderMonitorService"); }

    //endregion

    //region public methods

    @Override
    public void onCreate() {
        super.onCreate();

        FullyBellyApplication.component().inject(this);
    }

    //endregion

    //region internal methods

    @Override
    protected void onHandleIntent(Intent intent) {
        String sessionId = intent.getStringExtra(SESSION_ID_KEY);

        monitorService.configure(sessionId);

        for (int i = 0; i < 4; i++) {
            monitorService.run();

            OrdersMonitorServiceResult result = monitorService.getResults();

            if (result.getResultCode() == ServiceResultCodes.SERVICE_OK) {
                EventBus.getDefault().post(result);

                return;
            }

            SystemClock.sleep(5000);
        }

        EventBus.getDefault().post(
                new OrdersMonitorServiceResult(
                        new FreshOrderInfo(sessionId), ServiceResultCodes.NO_DATA));
    }

    //endregion

}
