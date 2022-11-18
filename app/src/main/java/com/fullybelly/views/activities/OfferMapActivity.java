package com.fullybelly.views.activities;

import android.os.Bundle;

import com.fullybelly.R;
import com.fullybelly.events.activities.OfferMapActivityInitialized;
import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.services.util.NavigationPayloadNames;

import org.greenrobot.eventbus.EventBus;

public class OfferMapActivity extends BaseActivity {

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
;
        setContentView(R.layout.activity_offer_map);
        EventBus.getDefault().postSticky(new OfferMapActivityInitialized(
                (DetailedOffer)getIntent().getExtras().getSerializable(NavigationPayloadNames.OFFER)
        ));
    }

    //endregion

}
