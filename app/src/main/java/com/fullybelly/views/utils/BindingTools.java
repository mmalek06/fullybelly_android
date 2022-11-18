package com.fullybelly.views.utils;

import android.databinding.BindingAdapter;
import android.support.design.widget.TextInputLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public final class BindingTools {

    //region public methods

    @BindingAdapter("app:errorText")
    public static void setErrorText(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }

    //endregion

}
