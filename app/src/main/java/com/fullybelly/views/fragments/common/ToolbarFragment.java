package com.fullybelly.views.fragments.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentToolbarBinding;
import com.fullybelly.services.results.OrdersMonitorServiceResult;
import com.fullybelly.services.results.ServiceResultCodes;
import com.fullybelly.viewModels.fragments.TopToolbarViewModel;
import com.fullybelly.views.animation.FlashAnimation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class ToolbarFragment extends Fragment {

    //region public fields

    @Inject
    public TopToolbarViewModel dataContext;

    //endregion

    //region constructor

    public ToolbarFragment() { }

    //endregion

    //region public methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getContext();
        FragmentToolbarBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_toolbar,
                container,
                false);

        FullyBellyApplication.component().inject(this);
        binding.setDataContext(dataContext);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        dataContext.init();
    }

    @Override
    public void onPause() {
        getView().findViewById(R.id.newOrdersIco).clearAnimation();
        EventBus.getDefault().unregister(this);
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

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrdersMonitorServiceResult(OrdersMonitorServiceResult result) {
        View newOrdersView = getView().findViewById(R.id.newOrdersIco);

        if (result.getResultCode() == ServiceResultCodes.SERVICE_OK) {
            FlashAnimation.flash(newOrdersView);
        }
    }

    //endregion

}
