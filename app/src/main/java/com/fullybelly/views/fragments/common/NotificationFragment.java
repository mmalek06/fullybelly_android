package com.fullybelly.views.fragments.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fullybelly.R;
import com.fullybelly.services.local.implementations.DialogServiceImpl;

public class NotificationFragment extends DialogFragment {

    //region constants

    private final static String TEXT = "text";

    private final static String INFO = "info";

    private final static String LEVEL = "level";

    //endregion

    //region constructor

    public NotificationFragment() { }

    public static NotificationFragment newInstance(String text, String info, DialogServiceImpl.DialogLevel level) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();

        args.putString(TEXT, text);
        args.putString(INFO, info);
        args.putSerializable(LEVEL, level);
        fragment.setArguments(args);

        return fragment;
    }

    //endregion

    //region public methods

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();
        final LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.fragment_notification, null, false);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        TextView textStd = (TextView)dialogView.findViewById(R.id.notificationTextStd);
        TextView textWarn = (TextView)dialogView.findViewById(R.id.notificationTextWarn);
        TextView info = ((TextView)dialogView.findViewById(R.id.notificationInfo));
        DialogServiceImpl.DialogLevel level = (DialogServiceImpl.DialogLevel)getArguments().getSerializable(LEVEL);

        switch (level) {
            case STD:
                textWarn.setVisibility(View.GONE);
                break;
            case WARN:
                textStd.setVisibility(View.GONE);
                break;
        }

        textStd.setText(getArguments().getString(TEXT));
        textWarn.setText(getArguments().getString(TEXT));
        info.setText(getArguments().getString(INFO));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }


    //endregion

}
