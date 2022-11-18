package com.fullybelly.views.fragments.ordersActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.viewModels.fragments.OrdersListViewModel;
import com.fullybelly.views.adapters.OrdersAdapter;

import javax.inject.Inject;

public class OrdersListFragment extends Fragment {

    //region fields

    @Inject
    public OrdersListViewModel dataContext;

    //endregion

    //region public methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            FullyBellyApplication.component().inject(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new OrdersAdapter(dataContext));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataContext.loadOrders();
    }

    @Override
    public void onPause() {
        dataContext.sleep();
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        dataContext.kill();

        dataContext = null;

        super.onDestroyView();
    }

    //endregion

}
