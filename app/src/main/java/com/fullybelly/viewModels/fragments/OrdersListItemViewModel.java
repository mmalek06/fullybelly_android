package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableField;

import com.fullybelly.models.api.PaidOrder;
import com.fullybelly.viewModels.base.BaseViewModel;

public final class OrdersListItemViewModel extends BaseViewModel {

    //region properties

    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> dishesAmount = new ObservableField<String>();

    public ObservableField<String> pickupTime = new ObservableField<>();

    public ObservableField<String> number = new ObservableField<>();

    //endregion

    //region fields

    private PaidOrder mOrder;

    //endregion

    //region public methods

    public void setOrder(PaidOrder paidOrder) {
        name.set(paidOrder.getName());
        dishesAmount.set(String.valueOf(paidOrder.getDishesAmount()));
        pickupTime.set(paidOrder.getPickupTime());
        number.set(paidOrder.getNumber());

        mOrder = paidOrder;
    }

    //endregion

}
