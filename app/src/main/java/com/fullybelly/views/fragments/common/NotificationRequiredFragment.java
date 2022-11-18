package com.fullybelly.views.fragments.common;

import android.app.Dialog;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fullybelly.R;
import com.fullybelly.events.activities.NotificationRequiredAccepted;
import com.fullybelly.events.activities.NotificationRequiredDeclined;
import com.fullybelly.views.utils.taghandlers.OlTagHandler;

import org.greenrobot.eventbus.EventBus;

public class NotificationRequiredFragment extends DialogFragment {

    //region constants

    private final static String TEXT = "text";

    private final static String INFO = "info";

    private final static String IS_HTML = "is-html";

    private final static String CONFIRMATION_REQUIRED = "confirmation-required";

    //endregion

    //region constructor

    public NotificationRequiredFragment() { }

    public static NotificationRequiredFragment newInstance(String text, String info, boolean isHtml, boolean confirmationRequired) {
        NotificationRequiredFragment fragment = new NotificationRequiredFragment();
        Bundle args = new Bundle();

        args.putString(TEXT, text == null ? "" : text);
        args.putString(INFO, info);
        args.putBoolean(IS_HTML, isHtml);
        args.putBoolean(CONFIRMATION_REQUIRED, confirmationRequired);
        fragment.setArguments(args);

        return fragment;
    }

    //endregion

    //region public methods

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();
        final LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.fragment_notification_required, null, false);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        TextView textView = (TextView)dialogView.findViewById(R.id.notificationText);
        TextView info = ((TextView)dialogView.findViewById(R.id.notificationInfo));
        View actionButtons = dialogView.findViewById(R.id.actionButtons);

        if (!getArguments().getString(TEXT).equals("")) {
            textView.setText(getArguments().getString(TEXT));
        } else {
            textView.setVisibility(View.GONE);
        }

        if (getArguments().getBoolean(IS_HTML)) {
            info.setText(Html.fromHtml(getArguments().getString(INFO), null, new OlTagHandler()));
        } else {
            info.setText(getArguments().getString(INFO));
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setEvents(dialogView);

        if (!getArguments().getBoolean(CONFIRMATION_REQUIRED)) {
            actionButtons.setVisibility(View.GONE);
        }

        return dialog;
    }

    //endregion

    //region private methods

    private void setEvents(final View view) {
        Button cancelButton = (Button)view.findViewById(R.id.declineButton);
        Button okButton = (Button)view.findViewById(R.id.acceptButton);
        Button scrollButton = (Button)view.findViewById(R.id.scrollDownButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new NotificationRequiredDeclined());
                dismissAllowingStateLoss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new NotificationRequiredAccepted());
                dismissAllowingStateLoss();
            }
        });
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView scroll = (ScrollView)view.findViewById(R.id.notificationInfoWrapper);

                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //endregion

}
