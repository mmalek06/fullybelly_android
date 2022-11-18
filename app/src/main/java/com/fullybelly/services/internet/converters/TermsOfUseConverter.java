package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.TermsOfUse;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.services.results.TermsOfUseServiceResult;

import org.json.JSONException;
import org.json.JSONObject;

public final class TermsOfUseConverter extends BaseConverter<TermsOfUse, TermsOfUseServiceResult> {

    //region internal methods

    @Override
    protected TermsOfUseServiceResult getResult(TermsOfUse input, ServiceResultCodes code) {
        return new TermsOfUseServiceResult(input, code);
    }

    @Override
    protected TermsOfUseServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        TermsOfUse terms = fromJson(json);

        if (terms != null) {
            return getResult(terms, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    private TermsOfUse fromJson(JSONObject json) {
        try {
            int id = json.getInt("id");
            String text = json.getString("text");
            String impressum = json.getString("impressum");

            return new TermsOfUse(id, text, impressum, false);
        } catch (JSONException e) {
            return null;
        }
    }

    //endregion

}
