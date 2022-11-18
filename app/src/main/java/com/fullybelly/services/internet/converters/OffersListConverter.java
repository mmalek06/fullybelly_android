package com.fullybelly.services.internet.converters;

import com.fullybelly.models.api.Offer;
import com.fullybelly.services.results.JsonResult;
import com.fullybelly.services.results.OffersServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class OffersListConverter {

    //region fields

    private static final HashSet<Integer> ERROR_CODES =
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

    public OffersServiceResult convert(JsonResult result, String scheme, String root, Double latitude, Double longitude)
            throws JSONException {
        if (result == null) {
            return getResult(null, null, null, ServiceResultCodes.NO_DATA);
        }

        int resultCode = result.getResultCode();

        if (OffersListConverter.ERROR_CODES.contains(resultCode)) {
            return getResult(null, null, null, ServiceResultCodes.NO_DATA);
        }

        Object json = result.getResult();

        if (json instanceof JSONObject) {
            return getConvertedResult((JSONObject)json, scheme, root, latitude, longitude);
        }

        throw new JSONException("not implemented json type");
    }

    //endregion

    //region methods

    protected OffersServiceResult getResult(List<Offer> input, Double latitude, Double longitude, ServiceResultCodes code) {
        return new OffersServiceResult(input, latitude, longitude, code);
    }

    protected OffersServiceResult getConvertedResult(JSONObject json, String scheme, String root, Double latitude, Double longitude) {
        Iterator<String> iter = json.keys();
        List<Offer> offers = null;

        while (iter.hasNext()) {
            String key = iter.next();

            if (key.equalsIgnoreCase("results")) {
                offers = getOffers(json, key, scheme, root);
            }
        }

        if (offers != null) {
            return getResult(offers, latitude, longitude, ServiceResultCodes.SERVICE_OK);
        } else {
            return getResult(null, latitude, longitude, ServiceResultCodes.NO_DATA);
        }
    }

    private List<Offer> getOffers(JSONObject json, String key, String scheme, String root) {
        List<Offer> offers = new ArrayList<>();

        try {
            JSONArray results = (JSONArray)json.get(key);

            for (int i = 0; i < results.length(); i++) {
                JSONObject row = results.getJSONObject(i);
                int restaurantId = row.getInt("restaurant_id");
                int mealId = row.getString("meal_id").equals("null") ? 0 : row.getInt("meal_id");
                String mealDescription = row.isNull("meal_description") ? null : row.getString("meal_description");
                String name = row.getString("name");
                String logo = row.getString("logo");
                String logoUrl = logo.startsWith("http") ? logo : String.format("%s://%s%s", scheme, root, logo);
                Double distance = row.isNull("distance") ? null : row.getDouble("distance");
                int availableDishes = row.getInt("available_dishes");
                double standardPrice = row.getDouble("normal_price");
                double discountedPrice = row.getDouble("discount_price");
                String currency = row.getString("currency");
                String pickupFrom = row.getString("pickup_from");
                String pickupTo = row.getString("pickup_to");
                boolean isTodaysOffer = row.getBoolean("todays_offer");

                offers.add(new Offer(restaurantId, mealId, name, logoUrl, distance, availableDishes, standardPrice, discountedPrice, currency, pickupFrom, pickupTo, mealDescription, isTodaysOffer));
            }
        } catch (JSONException e) {
            return null;
        }

        return offers;
    }

    //endregion

}
