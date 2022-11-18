package com.fullybelly.services.local.implementations;

import android.content.Context;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.services.base.BaseAppService;
import com.fullybelly.services.local.interfaces.TextService;

public final class TextServiceImpl extends BaseAppService implements TextService {

    //region constructor

    public TextServiceImpl(FullyBellyApplication application) { super(application); }

    //endregion

    //region public methods

    @Override
    public String get(int id) {
        Context context = getContext();

        if (context == null) {
            return "";
        }

        return context.getString(id);
    }

    //endregion

}
