package com.fullybelly.services.internet.base;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.results.JsonResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BasePostInternetService extends BaseInternetService {

    //region constructor

    public BasePostInternetService(FullyBellyApplication application) {
        super(application);
    }

    //endregion

    //region methods

    @Override
    protected JsonResult getData(URL url) throws IOException, JSONException {
        JSONObject request = getRequest();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.connect();

        OutputStream os = conn.getOutputStream();

        os.write(request.toString().getBytes());
        os.flush();

        int statusCode = conn.getResponseCode();

        if (statusCode == HttpURLConnection.HTTP_CREATED || statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            return getHttpResult(statusCode, conn);
        } else {
            return new JsonResult(statusCode, null);
        }
    }

    protected abstract JSONObject getRequest() throws JSONException;

    //endregion

}
