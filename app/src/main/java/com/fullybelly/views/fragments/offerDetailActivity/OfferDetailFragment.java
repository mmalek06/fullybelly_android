package com.fullybelly.views.fragments.offerDetailActivity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentOfferDetailBinding;
import com.fullybelly.events.offerDetail.DetailsLoadCompleted;
import com.fullybelly.models.api.DetailedOffer;
import com.fullybelly.viewModels.fragments.OfferDetailsViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class OfferDetailFragment extends Fragment {

    //region public fields

    @Inject
    public OfferDetailsViewModel dataContext;

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfferLoaded(DetailedOffer offer) {
        if (offer == null) {
            return;
        }
        if (offer.getLogoUrl() != null) {
            String url = offer.getLogoUrl();
            ImageView imageView = (ImageView)getView().findViewById(R.id.restaurantLogo);

            setWebImage(imageView, url);
        }
        if (offer.getBackgroundImgUrl() != null) {
            String url = offer.getBackgroundImgUrl();
            ImageView imageView = (ImageView)getView().findViewById(R.id.restaurantBackground);

            setWebImage(imageView, url);
        }
    }

    //endregion

    //region lifecycle events

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getContext();
        FragmentOfferDetailBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_offer_detail,
                container,
                false);

        FullyBellyApplication.component().inject(this);
        binding.setDataContext(dataContext);
        binding.setParent(this);

        View view = binding.getRoot();
        TextView tv = (TextView)view.findViewById(R.id.detailOldPrice);

        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataContext == null) {
            FullyBellyApplication.component().inject(this);
        }

        dataContext.init();
    }

    @Override
    public void onPause() {
        dataContext.sleep();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        dataContext.kill();

        dataContext = null;

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //endregion

    //region event handlers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfferLoaded(DetailsLoadCompleted message) {
        if (message.getName() != null) {
            getActivity().setTitle(message.getName());
        }
    }

    //endregion

    //region public methods

    public void showPurchaseDialog() {
        int restaurantId = dataContext.getRestaurantId();
        int mealId = dataContext.getMealId();
        int maxAmount = dataContext.getAvailableDishesCount();
        double unitPrice = dataContext.getUnitPrice();
        String pickupFrom = dataContext.getPickupFrom();
        String pickupTo = dataContext.getPickupTo();
        String currency = dataContext.getCurrency();

        DialogFragment newFragment = PurchaseDetailsDialogFragment.newInstance(restaurantId, mealId, maxAmount, unitPrice, currency, pickupFrom, pickupTo);
        newFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "purchase");
    }

    //endregion

    //region private methods

    private void setWebImage(ImageView view, String url) {
        Glide.with(getContext())
             .load(url)
             .into(view);
    }

    //endregion

}
