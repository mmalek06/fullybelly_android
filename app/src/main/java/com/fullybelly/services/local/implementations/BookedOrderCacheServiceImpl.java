package com.fullybelly.services.local.implementations;

import android.content.Context;
import android.content.SharedPreferences;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.models.api.BookedOrder;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.BookedOrderCacheService;

public final class BookedOrderCacheServiceImpl extends BaseAppService implements BookedOrderCacheService {

    //region constructor

    public BookedOrderCacheServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public void put(BookedOrder order) {
        Context context = getContext();

        if (context == null) {
            return;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getContext().getString(R.string.settingsBookedOrder), order.getSessionId());
        editor.apply();
    }

    @Override
    public BookedOrder pop() {
        Context context = getContext();

        if (context == null) {
            return null;
        }

        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getString(R.string.settingsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String sessionId = preferences.getString(getContext().getString(R.string.settingsBookedOrder), "");
        BookedOrder order = new BookedOrder(sessionId);

        editor.remove(getContext().getString(R.string.settingsBookedOrder));
        editor.apply();

        return order;
    }

    //endregion

}
