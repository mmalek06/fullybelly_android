package com.fullybelly.views.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.ActivityOfferDetailBinding;
import com.fullybelly.events.orders.OrderCheckRequested;
import com.fullybelly.events.activities.OfferDetailActivityInitialized;
import com.fullybelly.events.offerDetail.PaymentResult;
import com.fullybelly.events.offerDetail.PaymentStepFormRequested;
import com.fullybelly.services.local.interfaces.PaypalBackgroundServiceRunner;
import com.fullybelly.services.util.NavigationPayloadNames;
import com.fullybelly.viewModels.activities.OfferDetailsViewModel;
import com.fullybelly.views.fragments.offerDetailActivity.PaymentMethodChooserFragment;
import com.fullybelly.views.fragments.offerDetailActivity.UserDetailsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class OfferDetailActivity extends BaseActivity {

    //region public fields

    @Inject
    public OfferDetailsViewModel dataContext;

    @Inject
    public PaypalBackgroundServiceRunner ppBackgroundServiceRunner;

    //endregion

    //region private fields

    private PaymentStepFormRequested stepFormRequested;

    private ActivityResult mPaymentResult;

    private boolean mIsStateAlreadySaved = false;

    private boolean mPendingShowUserDetailsDialog = false;

    private boolean mPendingShowPaymentChoicesDialog = false;

    //endregion

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOfferDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_offer_detail);

        FullyBellyApplication.component().inject(this);
        binding.setDataContext(dataContext);
        ppBackgroundServiceRunner.run();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataContext == null) {
            FullyBellyApplication.component().inject(this);
        }

        dataContext.init();
    }

    @Override
    protected void onPause() {
        mIsStateAlreadySaved = true;

        dataContext.sleep();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ppBackgroundServiceRunner.stop();
        dataContext.kill();

        dataContext = null;

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mPaymentResult = new ActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        mIsStateAlreadySaved = false;

        if (mPendingShowUserDetailsDialog){
            mPendingShowUserDetailsDialog = false;

            showUserDetailsDialog();
        }
        if (mPendingShowPaymentChoicesDialog) {
            mPendingShowPaymentChoicesDialog = false;

            showPaymentMethodDialog();
        }
        if (mPaymentResult == null) {
            handleStandardResume();
        } else {
            handlePaymentGateReturn();
        }
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentStepFormRequested(PaymentStepFormRequested request) {
        stepFormRequested = request;

        if (request.getStep() == PaymentStepFormRequested.Step.USER_DETAILS) {
            showUserDetailsDialog();
        } else if (request.getStep() == PaymentStepFormRequested.Step.PAYMENT_METHOD) {
            showPaymentMethodDialog();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderCheckRequested(OrderCheckRequested request) {
        boolean sameCheckRunning = hasSameOrdersCheck(request.getSessionId());

        if (sameCheckRunning) {
            return;
        }

        monitorOrders(request.getSessionId());
    }

    //endregion

    //region private methods

    private void handleStandardResume() {
        double defaultLatLng = -10000000;
        int restaurantId = getIntent().getIntExtra(NavigationPayloadNames.RESTAURANT_ID, 0);
        int mealId = getIntent().getIntExtra(NavigationPayloadNames.MEAL_ID, 0);
        Double lat = getIntent().getDoubleExtra(NavigationPayloadNames.LATITUDE, defaultLatLng);
        Double lng = getIntent().getDoubleExtra(NavigationPayloadNames.LONGITUDE, defaultLatLng);
        Double latitude = lat != defaultLatLng ? lat : null;
        Double longitude = lng != defaultLatLng ? lng : null;

        EventBus.getDefault().postSticky(new OfferDetailActivityInitialized(restaurantId, mealId, latitude, longitude));
    }

    private void handlePaymentGateReturn() {
        int resultCode = mPaymentResult.resultCode;
        int requestCode = mPaymentResult.requestCode;
        Intent data = mPaymentResult.data;

        mPaymentResult = null;

        boolean success = false;

        if (resultCode == RESULT_OK) {
            success = true;
        }

        EventBus.getDefault().post(new PaymentResult(data, success, requestCode, resultCode));
    }

    private void showUserDetailsDialog() {
        if(mIsStateAlreadySaved){
            mPendingShowUserDetailsDialog = true;
        } else {
            DialogFragment newFragment = UserDetailsFragment.newInstance(stepFormRequested);
            newFragment.show(getSupportFragmentManager(), "userDetailsForm");
        }
    }

    private void showPaymentMethodDialog() {
        if (mIsStateAlreadySaved) {
            mPendingShowPaymentChoicesDialog = true;
        } else {
            DialogFragment newFragment = PaymentMethodChooserFragment.newInstance(stepFormRequested);
            newFragment.show(getSupportFragmentManager(), "paymentMethodChooserForm");
        }
    }

    private Bundle getMetadata() {
        ApplicationInfo app;

        try { app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA); }
        catch (PackageManager.NameNotFoundException e) { return null; }

        return app.metaData;
    }

    private boolean hasSameOrdersCheck(String sessionId) {
        for (String savedId : runningSessionIdsChecks) {
            if (savedId.equals(sessionId)) {
                return true;
            }
        }

        return false;
    }

    //endregion

    //region private classes

    private class ActivityResult {

        //region properties

        public int requestCode;

        public int resultCode;

        public Intent data;

        //endregion

        //region constructor

        public ActivityResult(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }

        //endregion

    }

    //endregion

}
