package com.fullybelly.viewModels.fragments;

import android.databinding.Bindable;
import android.databinding.ObservableBoolean;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.events.offerDetail.DetailsLoadCompleted;
import com.fullybelly.events.offerDetail.DetailsLoadStarted;
import com.fullybelly.events.activities.OfferDetailActivityInitialized;
import com.fullybelly.events.offerDetail.PaymentResult;
import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.internet.offers.OfferDetailsService;
import com.fullybelly.services.internet.offers.OfferDetailsServiceImpl;
import com.fullybelly.services.local.implementations.DialogServiceImpl;
import com.fullybelly.services.local.interfaces.DialogService;
import com.fullybelly.services.local.interfaces.PaymentResultConsumer;
import com.fullybelly.services.local.interfaces.PaymentResultConsumerFactory;
import com.fullybelly.services.local.interfaces.TextService;
import com.fullybelly.services.results.OfferDetailsServiceResult;
import com.fullybelly.viewModels.base.BaseOfferViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import javax.inject.Inject;

public final class OfferDetailsViewModel extends BaseOfferViewModel<DetailedOffer> implements
        OfferDetailsServiceImpl.ServiceCallback {

    //region fields

    private final DialogService mDialogService;

    private final OfferDetailsService mDetailsService;

    private final PaymentResultConsumerFactory mPaymentResultConsumerFactory;

    private int mRestaurantId;

    private int mMealId;

    private Double mLatitude;

    private Double mLongitude;

    private boolean mCanBuy;

    //endregion

    //region properties

    @Bindable
    public String getDistrict() { return mOffer.getDistrict(); }

    @Bindable
    public String getAddress() { return mOffer.getAddress(); }

    @Bindable
    public String getDescription() { return mOffer.getDescription(); }

    @Bindable
    public boolean getCanBuy() {
        return mCanBuy && isOfferForToday();
    }

    @Bindable
    public boolean getHasDescription() {
        String desc = mOffer.getDescription();

        if (desc == null) {
            desc = "";
        }

        return desc.trim() != "";
    }

    @Bindable
    public String getPickupTimes() { return mOffer.getPickupFrom() + " - " + mOffer.getPickupTo(); }

    @Bindable
    public boolean getCanEatOnSite() { return mOffer.canEatOnSite(); }

    //endregion

    //region getters

    public int getRestaurantId() { return mRestaurantId; }

    public int getMealId() { return mMealId; }

    public String getCurrency() { return mOffer.getCurrency(); }

    //endregion

    //region constructor

    @Inject
    public OfferDetailsViewModel(TextService textService,
                                 DialogService dialogService,
                                 OfferDetailsService detailsService,
                                 PaymentResultConsumerFactory paymentResultConsumerFactory) {
        super(textService);

        mOffer = new DetailedOffer();
        mDialogService = dialogService;
        mDetailsService = detailsService;
        mPaymentResultConsumerFactory = paymentResultConsumerFactory;
    }

    //endregion

    //region public methods

    public void init() { setUpMessaging(); }

    @Override
    public void kill() {
        super.kill();
        mDetailsService.tearDown();
    }

    @Override
    public void gotOffer(OfferDetailsServiceResult result) {
        setOffer(result.getOffer());
        EventBus.getDefault().post(new DetailsLoadCompleted(result.getOffer().getName()));
    }

    @Override
    public void gotError(OfferDetailsServiceResult result) {
        switch (result.getResultCode()) {
            case NO_DATA:
            case BAD_DATA_RECEIVED:
            case WRONG_ARGUMENT:
            case IMPROPERLY_CONFIGURED:
                String header = mTextService.get(R.string.noRestaurantDetailsInfo);
                String explanation = mTextService.get(R.string.tryDifferentCriteria);

                mDialogService.showDialog(header, explanation, DialogServiceImpl.DialogLevel.WARN);
                break;
            case NO_PERMISSIONS:
                mDialogService.showDialog(
                        mTextService.get(R.string.permissionsDenied),
                        mTextService.get(R.string.addPermissionsAndTryAgain),
                        DialogServiceImpl.DialogLevel.WARN);
                break;
            case NO_PROVIDER_AVAILABLE:
                mDialogService.showDialog(
                        mTextService.get(R.string.providerUnavailable),
                        mTextService.get(R.string.providerUnavailableExplanation),
                        DialogServiceImpl.DialogLevel.WARN);
                break;
        }

        EventBus.getDefault().post(new DetailsLoadCompleted(null));
    }

    public void loadOffer() {
        EventBus.getDefault().post(new DetailsLoadStarted());

        mDetailsService.configure(
                this,
                mLatitude,
                mLongitude,
                mRestaurantId,
                mMealId).run();
    }

    public void loadOffer(int restaurantId, int mealId, Double latitude, Double longitude) {
        mRestaurantId = restaurantId;
        mMealId = mealId;
        mLatitude = latitude;
        mLongitude = longitude;

        loadOffer();
    }

    public void explainBuyForbidden() {
        if (isOfferForTomorrow()) {
            mDialogService.showToast(mTextService.get(R.string.purchaseForbiddenOfferTomorrow));
        } else {
            mDialogService.showToast(mTextService.get(R.string.purchaseForbidden));
        }
    }

    @Override
    public void setOffer(DetailedOffer offer) {
        super.setOffer(offer);

        if (offer.getCountry().equalsIgnoreCase(FullyBellyApplication.getInstance().getCountryCode())) {
            mCanBuy = true;
        }

        notifyChange();
        EventBus.getDefault().post(offer);
    }

    public int getAvailableDishesCount() { return mOffer.getAvailableDishes(); }

    public double getUnitPrice() { return mOffer.getDiscountedPrice(); }

    public boolean isOfferForToday() { return mOffer.getIsTodaysOffer() && mOffer.getMealId() != 0; }

    public boolean isOfferForTomorrow() { return !mOffer.getIsTodaysOffer() && mOffer.getMealId() != 0; }

    public boolean isOffer() { return mOffer.getMealId() != 0; }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityCreated(OfferDetailActivityInitialized message) {
        loadOffer(message.getRestaurantId(),
                  message.getMealId(),
                  message.getLatitude(),
                  message.getLongitude());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentResult(PaymentResult result) {
        loadOffer();

        PaymentResultConsumer consumer = mPaymentResultConsumerFactory.get(result);

        if (consumer != null) {
            consumer.consume(result);
        }
    }

    //endregion

}
