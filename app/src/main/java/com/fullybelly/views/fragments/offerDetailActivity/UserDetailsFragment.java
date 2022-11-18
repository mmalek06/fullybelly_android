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
import com.fullybelly.databinding.FragmentUserDetailsBinding;
import com.fullybelly.events.offerDetail.PaymentStepFormRequested;
import com.fullybelly.viewModels.fragments.UserDetailsViewModel;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class UserDetailsFragment extends DialogFragment {

    //region constants

    protected static final String RESTAURANT_ID = "restaurant-id";

    protected static final String MEAL_ID = "meal-id";

    protected static final String CHOSEN_AMOUNT = "chosen-amount";

    protected static final String TRANSACTION_VALUE = "transaction-value";

    protected static final String CURRENCY = "currency";

    //endregion

    //region public fields

    @Inject
    public UserDetailsViewModel dataContext;

    //endregion

    //region constructor

    public UserDetailsFragment() { }

    public static UserDetailsFragment newInstance(PaymentStepFormRequested request) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();

        args.putInt(RESTAURANT_ID, request.getRestaurantId());
        args.putInt(MEAL_ID, request.getMealId());
        args.putInt(TRANSACTION_VALUE, request.getTransactionValue());
        args.putInt(CHOSEN_AMOUNT, request.getAmount());
        args.putString(CURRENCY, request.getCurrency());
        fragment.setArguments(args);

        return fragment;
    }

    //endregion

    //region lifecycle events

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        FragmentUserDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_user_details,
                null,
                false);
        int restaurantId = getArguments().getInt(RESTAURANT_ID);
        int mealId = getArguments().getInt(MEAL_ID);
        int transactionValue = getArguments().getInt(TRANSACTION_VALUE);
        int chosenAmount = getArguments().getInt(CHOSEN_AMOUNT);
        String currency = getArguments().getString(CURRENCY);

        FullyBellyApplication.component().inject(this);
        dataContext.init(restaurantId,
                mealId,
                chosenAmount,
                transactionValue,
                currency);
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
