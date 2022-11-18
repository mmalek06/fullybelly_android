package com.fullybelly.views.fragments.offerDetailActivity;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentPurchaseDetailsDialogBinding;
import com.fullybelly.viewModels.fragments.PurchaseDetailsViewModel;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PurchaseDetailsDialogFragment extends DialogFragment {

    //region constants

    private static final String RESTAURANT_ID = "restaurantId";

    private static final String MEAL_ID = "mealId";

    private static final String MAX_AMOUNT = "maxAmount";

    private static final String UNIT_PRICE = "unitPrice";

    private static final String PICKUP_TIME_FROM = "pickupTimeFrom";

    private static final String PICKUP_TIME_TO = "pickupTimeTo";

    private static final String CURRENCY = "currency";

    //endregion

    //region public fields

    @Inject
    public PurchaseDetailsViewModel dataContext;

    //endregion

    //region constructor

    public static PurchaseDetailsDialogFragment newInstance(
            int restaurantId,
            int mealId,
            int maxAmount,
            double unitPrice,
            String currency,
            String pickupTimeFrom,
            String pickupTimeTo) {
        PurchaseDetailsDialogFragment fragment = new PurchaseDetailsDialogFragment();
        Bundle args = new Bundle();

        args.putInt(RESTAURANT_ID, restaurantId);
        args.putInt(MEAL_ID, mealId);
        args.putInt(MAX_AMOUNT, maxAmount);
        args.putDouble(UNIT_PRICE, unitPrice);
        args.putString(PICKUP_TIME_FROM, pickupTimeFrom);
        args.putString(PICKUP_TIME_TO, pickupTimeTo);
        args.putString(CURRENCY, currency);
        fragment.setArguments(args);

        return fragment;
    }

    //endregion

    //region lifecycle events

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        FragmentPurchaseDetailsDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_purchase_details_dialog,
                null,
                false);
        int restaurantId = getArguments().getInt(RESTAURANT_ID);
        int mealId = getArguments().getInt(MEAL_ID);
        int maxAmount = getArguments().getInt(MAX_AMOUNT);
        double unitPrice = getArguments().getDouble(UNIT_PRICE);
        String pickupFrom = getArguments().getString(PICKUP_TIME_FROM);
        String pickupTo = getArguments().getString(PICKUP_TIME_TO);
        String currency = getArguments().getString(CURRENCY);

        FullyBellyApplication.component().inject(this);
        dataContext.init(restaurantId,
                         mealId,
                         maxAmount,
                         unitPrice,
                         currency,
                         pickupFrom,
                         pickupTo);
        binding.setDataContext(dataContext);
        binding.setParent(this);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
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

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    //endregion

    //region public methods

    public void nextStep() {
        if (dataContext.canMakeNextStep()) {
            dataContext.makeNextStep();
            dismiss();
        }
    }

    //endregion

}
