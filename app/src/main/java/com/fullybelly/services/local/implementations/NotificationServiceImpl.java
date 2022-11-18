package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.widget.Toast;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.NotificationService;

public final class NotificationServiceImpl extends BaseAppService implements NotificationService {

    //region constants

    private static final int LENGTH_SHORT = 0;

    private static final int LENGTH_LONG = 1;

    //endregion

    //region constructor

    public NotificationServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void showNotification(String text) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotification(String text, int length) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        switch (length) {
            case LENGTH_SHORT:
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                return;
            case LENGTH_LONG:
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                return;
        }
    }

    //endregion

}
