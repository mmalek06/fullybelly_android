package com.fullybelly.viewModels.fragments;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.text.TextUtils;

import com.fullybelly.events.orders.OrdersListLoaded;
import com.fullybelly.models.api.PaidOrder;
import com.fullybelly.services.internet.orders.interfaces.OrdersListService;
import com.fullybelly.services.local.interfaces.OrdersNotificationService;
import com.fullybelly.services.local.interfaces.OrdersSessionsService;
import com.fullybelly.services.local.interfaces.UserDetailsService;
import com.fullybelly.services.results.OrdersListServiceResult;
import com.fullybelly.viewModels.base.BaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class OrdersListViewModel extends BaseViewModel implements OrdersListService.ServiceCallback {

    //region properties

    private ObservableList<OrdersListItemViewModel> mOrders = new ObservableArrayList<>();

    //endregion

    //region fields

    private UserDetailsService mUserDetailsService;

    private OrdersSessionsService mSessionsService;

    private OrdersListService mOrdersListService;

    private OrdersNotificationService mOrdersNotificationService;

    //endregion

    //region constructor

    @Inject
    public OrdersListViewModel(UserDetailsService userDetailsService,
                               OrdersSessionsService sessionsService,
                               OrdersListService ordersListService,
                               OrdersNotificationService ordersNotificationService) {
        mUserDetailsService = userDetailsService;
        mSessionsService = sessionsService;
        mOrdersListService = ordersListService;
        mOrdersNotificationService = ordersNotificationService;
    }

    //endregion

    //region public methods

    @Override
    public void kill() {
        super.kill();
        mOrdersListService.tearDown();
    }

    public ObservableList<OrdersListItemViewModel> getOrders() { return mOrders; }

    public OrdersListItemViewModel getOrder(int position) {
        return mOrders.get(position);
    }

    @Override
    public void gotOrders(OrdersListServiceResult result) {
        mOrders.clear();
        mOrdersNotificationService.removeOrderPending();

        List<OrdersListItemViewModel> orders = new ArrayList<>();
        List<String> sessions = new ArrayList<>();

        for (PaidOrder order : result.getOrders()) {
            OrdersListItemViewModel vm = new OrdersListItemViewModel();

            vm.setOrder(order);
            orders.add(vm);
            sessions.add(order.getSessionId());
        }

        mSessionsService.overwriteBookedSessions(sessions);
        mOrders.addAll(orders);
        EventBus.getDefault().post(new OrdersListLoaded(true));
    }

    @Override
    public void gotError(OrdersListServiceResult result) { EventBus.getDefault().post(new OrdersListLoaded(false)); }

    public void loadOrders() {
        mOrdersListService.configure(
                mUserDetailsService.getEmailAddress(),
                TextUtils.join(",", mSessionsService.getSessions()),
                this).run();
    }

    //endregion

}
