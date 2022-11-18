package com.fullybelly.views.activities;

import android.os.Bundle;
import android.view.View;

import com.fullybelly.R;
import com.fullybelly.events.orders.OrdersListLoaded;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class OrdersActivity extends BaseActivity {

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrdersListLoaded(OrdersListLoaded event) {
        findViewById(R.id.loaderFragmentWrapper).setVisibility(View.GONE);
    }

    //endregion

}
