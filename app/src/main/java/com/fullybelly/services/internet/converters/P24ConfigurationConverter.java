package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.P24Configuration;
import com.fullybelly.services.results.P24ConfigurationServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

public final class P24ConfigurationConverter extends BaseConverter<P24Configuration, P24ConfigurationServiceResult> {

    //region methods

    @Override
    protected P24ConfigurationServiceResult getResult(P24Configuration input, ServiceResultCodes code) {
        return new P24ConfigurationServiceResult(input, code);
    }

    @Override
    protected P24ConfigurationServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        P24Configuration p24Configuration = fromJson(json, scheme, root);

        if (p24Configuration != null) {
            return getResult(p24Configuration, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    private P24Configuration fromJson(JSONObject json, String scheme, String root) {
        try {
            String p24crc = json.getString("P_24_CRC");
            String p24merchant = json.getString("P_24_MERCHANT");

            return new P24Configuration(p24crc, p24merchant);
        } catch (JSONException e) {
            return null;
        }
    }

    //endregion

}
