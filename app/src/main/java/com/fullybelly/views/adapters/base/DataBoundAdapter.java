package com.fullybelly.views.adapters.base;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

abstract public class DataBoundAdapter<T extends ViewDataBinding> extends BaseDataBoundAdapter<T> {
    //region fields

    @LayoutRes
    private final int mLayoutId;

    //endregion

    //region constructor

    public DataBoundAdapter(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    //endregion

    //region public methods

    @Override
    public int getItemLayoutId(int position) {
        return mLayoutId;
    }

    //endregion

}
