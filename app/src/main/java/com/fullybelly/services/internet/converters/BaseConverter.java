package com.fullybelly.services.internet.converters;

import com.fullybelly.services.results.JsonResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;

public abstract class BaseConverter<T_IN, T_OUT> {

    //region fields

    protected static final HashSet<Integer> ERROR_CODES =
            new HashSet<>(Arrays.asList(new Integer[] {
                    HttpURLConnection.HTTP_NOT_FOUND,
                    HttpURLConnection.HTTP_BAD_METHOD,
                    HttpURLConnection.HTTP_NOT_ACCEPTABLE,
                    HttpURLConnection.HTTP_INTERNAL_ERROR,
                    HttpURLConnection.HTTP_NOT_IMPLEMENTED,
                    HttpURLConnection.HTTP_BAD_GATEWAY,
                    HttpURLConnection.HTTP_UNAVAILABLE,
                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT }));

    //endregion

    //region public methods

    public T_OUT convert(JsonResult result, String scheme, String root)
        throws JSONException {
        if (result == null) {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }

        int resultCode = result.getResultCode();

        if (BaseConverter.ERROR_CODES.contains(resultCode)) {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }

        Object json = result.getResult();

        if (json instanceof JSONObject) {
            return getConvertedResult((JSONObject)json, scheme, root);
        }
        if (json instanceof JSONArray) {
            return getConvertedResult((JSONArray)json, scheme, root);
        }

        throw new JSONException("not implemented json type");
    }

    //endregion

    //region protected methods

    protected T_OUT getConvertedResult(JSONObject json, String scheme, String root) { return null; }

    protected T_OUT getConvertedResult(JSONArray json, String scheme, String root) { return null; }

    protected abstract T_OUT getResult(T_IN input, ServiceResultCodes code);

    //endregion

}
