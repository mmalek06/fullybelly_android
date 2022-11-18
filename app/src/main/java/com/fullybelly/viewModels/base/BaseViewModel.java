package com.fullybelly.viewModels.base;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

public abstract class BaseViewModel extends BaseObservable {

    //region fields

    protected final Locale mCurrentLocale;

    //endregion

    //region constructor

    public BaseViewModel() {
        super();

        mCurrentLocale = Locale.getDefault();
    }

    //endregion

    //region public methods

    public void sleep() { EventBus.getDefault().unregister(this); }

    public void kill() { sleep(); }

    //endregion

    //region methods

    protected void setUpMessaging() { EventBus.getDefault().register(this); }

    //endregion

}
