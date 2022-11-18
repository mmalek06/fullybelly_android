package com.fullybelly.viewModels.base;

import android.databinding.Bindable;
import android.databinding.ObservableBoolean;

import com.fullybelly.R;
import com.fullybelly.models.api.Offer;
import com.fullybelly.services.local.interfaces.TextService;

import java.text.DecimalFormat;

public class BaseOfferViewModel<T_MODEL extends Offer> extends BaseViewModel {

    //region private fields

    protected T_MODEL mOffer;

    protected TextService mTextService;

    //endregion

    //region properties

    public ObservableBoolean hasDistance = new ObservableBoolean();

    public ObservableBoolean hasMealDescription = new ObservableBoolean();

    @Bindable
    public String getName() { return mOffer.getName(); }

    @Bindable
    public String getLogoUrl() { return mOffer.getLogoUrl(); }

    @Bindable
    public String getDistance() {
        Double dist = mOffer.getDistance();

        if (dist != null) {
            return Double.toString(dist) + "km";
        }

        return "";
    }

    @Bindable
    public String getAvailableDishes() { return Integer.toString(mOffer.getAvailableDishes()); }

    @Bindable
    public String getOldPrice() {
        double price = mOffer.getStandardPrice();

        if (price > 0) {
            DecimalFormat format = new DecimalFormat("0.00");

            return format.format(price) + mOffer.getCurrency();
        }

        return null;
    }

    @Bindable
    public String getPrice() {
        double price = mOffer.getDiscountedPrice();

        if (price > 0) {
            DecimalFormat format = new DecimalFormat("0.00");

            return format.format(price) + mOffer.getCurrency();
        }

        return "-";
    }

    @Bindable
    public String getPickupFrom() {
        String from = mTextService.get(R.string.from);
        String pickupFrom = mOffer.getPickupFrom();

        if (pickupFrom.equals("")) {
            pickupFrom = "-";
        }

        return from + " " + pickupFrom;
    }

    @Bindable
    public String getPickupTo() {
        String until = mTextService.get(R.string.until);
        String pickupTo = mOffer.getPickupTo();

        if (pickupTo.equals("")) {
            pickupTo = "-";
        }

        return until + " " + pickupTo;
    }

    @Bindable
    public String getMealDescription() {
        if (mOffer.getMealDescription() == null || mOffer.getMealDescription().length() == 0) {
            return "";
        }

        String description = mOffer.getMealDescription();

        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    @Bindable
    public boolean getIsTodaysOffer() {
        return mOffer.getIsTodaysOffer();
    }

    //endregion

    //endregion

    //region constructor

    public BaseOfferViewModel(TextService textService) { mTextService = textService; }

    //endregion

    //region public methods

    public void setOffer(T_MODEL model) {
        if (model.getDistance() != null) {
            hasDistance.set(true);
        }
        if (model.getMealDescription() != null && model.getMealDescription().length() > 0) {
            hasMealDescription.set(true);
        } else {
            hasMealDescription.set(false);
        }

        mOffer = model;
    }

    //endregion

}
