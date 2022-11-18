package com.fullybelly.services.base;

import android.content.Context;

import com.fullybelly.FullyBellyApplication;

public abstract class BaseAppService {

    //region fields

    protected FullyBellyApplication mApplication;

    //endregion

    //region constructor

    public BaseAppService(FullyBellyApplication application) { mApplication = application; }

    //endregion

    //region protected methods

    protected Context getContext() { return mApplication.getCurrentActivity(); }

    //endregion

}
