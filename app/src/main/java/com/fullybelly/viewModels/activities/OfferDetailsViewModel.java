package com.fullybelly.viewModels.activities;

import android.databinding.ObservableBoolean;

import com.fullybelly.events.offerDetail.DetailsLoadCompleted;
import com.fullybelly.events.offerDetail.DetailsLoadStarted;
import com.fullybelly.events.offerDetail.PaymentProcessingCompleted;
import com.fullybelly.events.offerDetail.PaymentProcessingStarted;
import com.fullybelly.viewModels.base.BaseViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public final class OfferDetailsViewModel extends BaseViewModel {

    //region observable fields

    public ObservableBoolean overlayLoader = new ObservableBoolean();

    public ObservableBoolean transparentLoader = new ObservableBoolean();

    //endregion

    //region event handlers

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onOfferLoadingCompleted(DetailsLoadCompleted event) { overlayLoader.set(false); }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onOfferLoadingStarted(DetailsLoadStarted event) { overlayLoader.set(true); }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPaymentProcessingStarted(PaymentProcessingStarted event) { transparentLoader.set(true); }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPaymentProcessingCompleted(PaymentProcessingCompleted event) { transparentLoader.set(false); }

    //endregion

    //region public methods

    public void init() {
        overlayLoader.set(true);
        setUpMessaging();
    }

    //endregion

}
