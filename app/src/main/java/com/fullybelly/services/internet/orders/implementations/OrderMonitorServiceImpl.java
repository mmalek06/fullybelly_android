package com.fullybelly.services.internet.orders.implementations;

import android.net.Uri;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.services.internet.base.BaseInternetService;
import com.fullybelly.services.internet.converters.OrderMonitorConverter;
import com.fullybelly.services.internet.orders.interfaces.OrderMonitorService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.JsonResult;
import com.fullybelly.services.results.OrdersMonitorServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class OrderMonitorServiceImpl extends BaseInternetService implements OrderMonitorService {

    //region constants

    private static final String API_VERSION = "v1";

    private static final String BASE_ENDPOINT = "orders";

    private static final String EXT_ENDPOINT = "monitor";

    //endregion

    //region fields

    private String mSessionId;

    private OrdersMonitorServiceResult mResult;

    //endregion

    //region constructor

    public OrderMonitorServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void tearDown() { }

    @Override
    public OrderMonitorServiceImpl configure(String sessionId) {
        if (!canRun()) {
            return this;
        }

        mSessionId = sessionId;

        super.setAddresses();

        return this;
    }

    @Override
    public OrdersMonitorServiceResult getResults() { return mResult; }

    //endregion

    //region internal methods

    @Override
    protected void internalRun() throws IOException, JSONException, MetadataException {
        URL url = getUrl();
        JsonResult json = getData(url);
        InternalResult result = new InternalResult(json, ServiceResultCodes.SERVICE_OK);

        process(result);
    }

    protected void process(InternalResult result) {
        if (result.getResultCode() != ServiceResultCodes.SERVICE_OK) {
            mResult = new OrdersMonitorServiceResult(null, result.getResultCode());
        } else {
            OrderMonitorConverter converter = new OrderMonitorConverter();

            try {
                OrdersMonitorServiceResult convertedResult = converter.convert(
                        result.getRequestResult(),
                        mSchemeStatic,
                        mRootStatic);

                if (convertedResult.getOrder() == null) {
                    mResult = new OrdersMonitorServiceResult(null, ServiceResultCodes.NO_DATA);
                } else {
                    mResult = convertedResult;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
                onError(ServiceResultCodes.BAD_DATA_RECEIVED);
            }
        }
    }

    @Override
    protected URL buildUrl() throws MalformedURLException {
        Uri.Builder builder =
                new Uri.Builder().scheme(mScheme)
                        .encodedAuthority(mRoot)
                        .appendPath(API_VERSION)
                        .appendPath(BASE_ENDPOINT)
                        .appendPath(EXT_ENDPOINT)
                        .appendEncodedPath(mSessionId + "/");
        String url = builder.toString();

        return new URL(url);
    }

    @Override
    protected void onError(ServiceResultCodes code) {
        mResult = new OrdersMonitorServiceResult(null, code);
    }

    //endregion

}
