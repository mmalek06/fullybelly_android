package com.fullybelly.views.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class DataBoundViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    //region fields

    public final T binding;

    //endregion

    //region constructor

    public DataBoundViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    //endregion

    //region public methods

    public static <T extends ViewDataBinding> DataBoundViewHolder<T> create(ViewGroup parent, @LayoutRes int layoutId) {
        T binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);

        return new DataBoundViewHolder<>(binding);
    }

    //endregion

}
