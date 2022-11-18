package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.results.OfferDetailsServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONException;
import org.json.JSONObject;

public final class OfferDetailConverter extends BaseConverter<DetailedOffer, OfferDetailsServiceResult> {

    //region internal methods

    @Override
    protected OfferDetailsServiceResult getConvertedResult(JSONObject json, String scheme, String root) {
        DetailedOffer offer = fromJson(json, scheme, root);

        if (offer != null) {
            return getResult(offer, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, ServiceResultCodes.NO_DATA);
        }
    }

    @Override
    protected OfferDetailsServiceResult getResult(DetailedOffer input, ServiceResultCodes code) {
        return new OfferDetailsServiceResult(input, code);
    }

    private DetailedOffer fromJson(JSONObject json, String scheme, String root) {
        try {
            String logo = json.getString("logo");
            String background = json.getString("background");
            String logoUrl = logo.startsWith("http") ? logo : String.format("%s://%s%s", scheme, root, logo);
            String backgroundUrl = background.startsWith("http") ? background : String.format("%s://%s%s", scheme, root, background);

            return new DetailedOffer(
                    json.getInt("restaurant_id"),
                    json.getInt("meal_id"),
                    json.getString("name"),
                    logoUrl,
                    backgroundUrl,
                    getDistance(json),
                    json.getString("city"),
                    getAddress(json),
                    json.getInt("available_dishes"),
                    json.getDouble("normal_price"),
                    json.getDouble("discount_price"),
                    json.getString("currency"),
                    json.getString("pickup_from"),
                    json.getString("pickup_to"),
                    getDescription(json),
                    json.getDouble("latitude"),
                    json.getDouble("longitude"),
                    getMealDescription(json),
                    json.getBoolean("meal_can_eat_on_site"),
                    json.getString("country"),
                    json.getBoolean("todays_offer")
            );
        } catch (JSONException e) {
            return null;
        }
    }

    private String getAddress(JSONObject json) throws JSONException {
        String city = json.getString("city");
        String street = json.getString("street");
        String stNumber = json.getString("street_number");
        String aptNumber = json.isNull("apartment_number") ? null : json.getString("apartment_number");
        String address;

        if (aptNumber != null) {
            address = String.format("%s, %s %s/%s", city, street, stNumber, aptNumber);
        } else {
            address = String.format("%s, %s %s", city, street, stNumber);
        }

        return address;
    }

    private Double getDistance(JSONObject json) throws JSONException {
        return json.isNull("distance") ? null : json.getDouble("distance");
    }

    private String getDescription(JSONObject json) throws JSONException {
        String description = json.isNull("description") ? null : json.getString("description");

        if (description == null) {
            description = "";
        }

        return description;
    }

    private String getMealDescription(JSONObject json) throws JSONException {
        return json.isNull("meal_description") ? null : json.getString("meal_description");
    }

    //endregion

}
