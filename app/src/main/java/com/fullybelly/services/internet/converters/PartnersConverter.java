package com.fullybelly.services.internet.converters;

import com.fullybelly.services.results.PartnersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class PartnersConverter extends BaseConverter<List<String>, PartnersServiceResult> {

    //region fields

    private String mScheme;

    private String mRoot;

    //endregion

    //region constructor

    public PartnersConverter(String scheme, String root) {
        mScheme = scheme;
        mRoot = root;
    }

    //endregion

    //region internal methods

    @Override
    protected PartnersServiceResult getResult(List<String> input, ServiceResultCodes code) {
        return new PartnersServiceResult(input, code);
    }

    @Override
    protected PartnersServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        List<String> partners = fromJson(json);

        if (partners != null) {
            return getResult(partners, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    private List<String> fromJson(JSONObject json) {
        try {
            List<String> partnersList = new ArrayList<>();
            JSONArray jsonPartners = json.getJSONArray("partners");

            if (jsonPartners != null) {
                for (int i=0; i < jsonPartners.length(); i++) {
                    String logo = jsonPartners.getString(i);
                    String logoUrl = logo.startsWith("http") ? logo : String.format("%s://%s%s", mScheme, mRoot, logo);

                    partnersList.add(logoUrl);
                }
            }

            return partnersList;
        } catch (JSONException e) {
            return null;
        }
    }

    //endregion

}
