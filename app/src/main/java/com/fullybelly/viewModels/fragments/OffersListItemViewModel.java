package com.fullybelly.viewModels.fragments;

import android.databinding.Bindable;

import com.fullybelly.R;
import com.fullybelly.models.api.Offer;
import com.fullybelly.services.local.interfaces.NavigationService;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.viewModels.base.BaseOfferViewModel;

public final class OffersListItemViewModel extends BaseOfferViewModel<Offer> {

    //region private fields

    private NavigationService mNavigationService;

    private Double mLatitude;

    private Double mLongitude;

    //endregion

    //region properties

    @Bindable
    @Override
    public String getPickupFrom() {
        String pickupFrom = mOffer.getPickupFrom();

        if (pickupFrom.equals("")) {
            pickupFrom = "-";
        }

        return pickupFrom;
    }

    @Bindable
    @Override
    public String getPickupTo() {
        String pickupTo = mOffer.getPickupTo();

        if (pickupTo.equals("")) {
            pickupTo = "-";
        }

        return pickupTo;
    }

    //endregion

    //region constructor

    public OffersListItemViewModel(NavigationService navigationService, TextService textService) {
        super(textService);

        mNavigationService = navigationService;
    }

    //endregion

    //region public methods

    public void setLocation(Double latitude, Double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void navigateToDetails() {
        mNavigationService.navigateToOfferDetails(
                mOffer.getRestaurantId(),
                mOffer.getMealId(),
                mLatitude,
                mLongitude);
    }

    //endregion

}
