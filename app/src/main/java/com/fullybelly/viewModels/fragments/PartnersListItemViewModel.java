package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableField;

public final class PartnersListItemViewModel {

    //region properties

    public ObservableField<String> logoUrl = new ObservableField<>();

    //endregion

    //region public methods

    public void setLogoUrl(String logoUrl) {
        this.logoUrl.set(logoUrl);
    }

    //endregion

}
