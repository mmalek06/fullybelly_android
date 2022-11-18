package com.fullybelly.views.fragments.offerDetailActivity;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentPaymentMethodChooserBinding;
import com.fullybelly.events.offerDetail.PaymentStepFormRequested;
import com.fullybelly.viewModels.fragments.PaymentMethodChooserViewModel;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PaymentMethodChooserFragment extends DialogFragment {

    //region constants

    protected static final String RESTAURANT_ID = "restaurant-id";

    protected static final String MEAL_ID = "meal-id";

    protected static final String CHOSEN_AMOUNT = "chosen-amount";

    protected static final String TRANSACTION_VALUE = "transaction-value";

    private static final String CURRENCY = "currency";

    //endregion

    //region fields

    private final Observable.OnPropertyChangedCallback dialogDismissPropertyCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable property, int i) {
            if (dataContext.dialogDismissRequested.get()) {
                dismiss();
            }
        }
    };

    //endregion

    //region public fields

    @Inject
    public PaymentMethodChooserViewModel dataContext;

    //endregion

    //region constructor

    public static PaymentMethodChooserFragment newInstance(PaymentStepFormRequested request) {
        PaymentMethodChooserFragment fragment = new PaymentMethodChooserFragment();
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
        FragmentPaymentMethodChooserBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_payment_method_chooser,
                null,
                false);
        int restaurantId = getArguments().getInt(RESTAURANT_ID);
        int mealId = getArguments().getInt(MEAL_ID);
        int transactionValue = getArguments().getInt(TRANSACTION_VALUE);
        int chosenAmount = getArguments().getInt(CHOSEN_AMOUNT);
        String currency = getArguments().getString(CURRENCY);

        FullyBellyApplication.component().inject(this);
        dataContext.dialogDismissRequested.addOnPropertyChangedCallback(dialogDismissPropertyCallback);
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
        dataContext.dialogDismissRequested.removeOnPropertyChangedCallback(dialogDismissPropertyCallback);
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
