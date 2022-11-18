package com.fullybelly.services.internet.base;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.exceptions.MetadataException;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.results.InternalResult;
import com.fullybelly.services.results.JsonResult;
import com.fullybelly.services.results.MetadataInfo;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.services.util.ApiInfo;
import com.fullybelly.services.util.MetadataHelper;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseInternetService extends BaseAppService implements InternetService {

    //region constants

    protected final static String TAG = "http-service";

    //endregion

    //region fields

    protected String mRootStatic;

    protected String mScheme;

    protected String mRoot;

    protected String mSchemeStatic;

    //endregion

    //region constructor

    public BaseInternetService(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void run() {
        if (!canRun()) {
            return;
        }

        try {
            internalRun();
        }
        catch (IOException e) {
            Log.d(TAG, e.getMessage());
            onError(ServiceResultCodes.IMPROPERLY_CONFIGURED);
        }
        catch (MetadataException e) {
            Log.d(TAG, e.getMessage());
            onError(ServiceResultCodes.IMPROPERLY_CONFIGURED);
        }
        catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            onError(ServiceResultCodes.BAD_DATA_RECEIVED);
        }
    }

    //endregion

    //region internal methods

    protected boolean canRun() {
        return getContext() != null;
    }

    protected void setAddresses() {
        MetadataInfo info = getMetadataInfo();
        mScheme = info.scheme;
        mRoot = info.apiRoot;
        mSchemeStatic = info.schemeStatic;
        mRootStatic = info.apiRootStatic;
    }

    protected JsonResult getHttpResult(int statusCode, HttpURLConnection conn) throws IOException, JSONException {
        InputStream is = null;

        try {
            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                is = new BufferedInputStream(conn.getInputStream());
            } else {
                is = new BufferedInputStream(conn.getErrorStream());
            }
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader buff = new BufferedReader(read);
            StringBuilder builder = new StringBuilder();
            String chunks;

            while ((chunks = buff.readLine()) != null) {
                builder.append(chunks);
            }

            String jsonString = builder.toString();
            JSONTokener tokener = new JSONTokener(jsonString);
            Object obj = tokener.nextValue();

            return new JsonResult(statusCode, obj);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    protected JsonResult getData(URL url) throws IOException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        int statusCode = conn.getResponseCode();

        if (statusCode ==  HttpURLConnection.HTTP_OK) {
            return getHttpResult(statusCode, conn);
        } else {
            return new JsonResult(statusCode, null);
        }
    }

    protected URL getUrl() throws MalformedURLException, MetadataException {
        URL url = buildUrl();

        if (url == null) {
            throw new MalformedURLException("URL is null");
        }

        return url;
    }

    protected MetadataInfo getMetadataInfo() {
        Bundle bundle = MetadataHelper.getMetadata(getContext());
        String scheme = bundle.getString(ApiInfo.API_SCHEME_KEY);
        String root = bundle.getString(ApiInfo.API_ROOT_KEY);
        String schemeStatic = bundle.getString(ApiInfo.API_SCHEME_STATIC_KEY);
        String rootStatic = bundle.getString(ApiInfo.API_ROOT_STATIC_KEY);

        return new MetadataInfo(scheme, root, schemeStatic, rootStatic);
    }

    protected abstract void internalRun() throws IOException, JSONException, MetadataException;

    protected abstract URL buildUrl() throws MalformedURLException;

    protected abstract void onError(ServiceResultCodes code);

    //endregion

    //region internal classes

    public abstract class AsyncRequest extends AsyncTask<Void, Void, InternalResult> {

        //region internal methods

        @Override
        protected InternalResult doInBackground(Void... params) {
            try {
                URL url = getUrl();
                JsonResult json = getData(url);

                return new InternalResult(json, ServiceResultCodes.SERVICE_OK);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                return new InternalResult(null, ServiceResultCodes.IMPROPERLY_CONFIGURED);
            } catch (MetadataException e) {
                Log.d(TAG, e.getMessage());
                return new InternalResult(null, ServiceResultCodes.IMPROPERLY_CONFIGURED);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
                return new InternalResult(null, ServiceResultCodes.BAD_DATA_RECEIVED);
            }
        }

        //endregion

    }

    //endregion

}
