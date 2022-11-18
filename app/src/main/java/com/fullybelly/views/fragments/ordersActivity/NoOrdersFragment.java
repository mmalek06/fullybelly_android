package com.fullybelly.views.fragments.ordersActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullybelly.R;
import com.fullybelly.events.orders.OrdersListLoaded;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NoOrdersFragment extends Fragment {

    //region constructor

    public NoOrdersFragment() { }

    //endregion

    //region public methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        return inflater.inflate(R.layout.fragment_no_orders, container, false);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrdersListLoaded(OrdersListLoaded event) {
        if (event.hasOrders()) {
            View view = getView().findViewById(R.id.noOrdersWrapper);

            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    //endregion

}
