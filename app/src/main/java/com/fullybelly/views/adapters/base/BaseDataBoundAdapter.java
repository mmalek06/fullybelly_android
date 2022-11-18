package com.fullybelly.views.adapters.base;

import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fullybelly.views.adapters.DataBoundViewHolder;

import java.util.List;

public abstract class BaseDataBoundAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<T>> {

    //region constants

    private static final Object DB_PAYLOAD = new Object();

    //endregion

    //region fields

    @Nullable
    private RecyclerView mRecyclerView;

    private final OnRebindCallback mOnRebindCallback = new OnRebindCallback() {
        @Override
        public boolean onPreBind(ViewDataBinding binding) {
            if (mRecyclerView == null || mRecyclerView.isComputingLayout()) {
                return true;
            }

            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(binding.getRoot());

            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }

            notifyItemChanged(childAdapterPosition, DB_PAYLOAD);

            return false;
        }
    };

    //endregion

    //region public methods

    @Override
    @CallSuper
    public DataBoundViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        DataBoundViewHolder<T> vh = DataBoundViewHolder.create(parent, viewType);

        vh.binding.addOnRebindCallback(mOnRebindCallback);

        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    @Override
    public final int getItemViewType(int position) {
        return getItemLayoutId(position);
    }

    @LayoutRes
    abstract public int getItemLayoutId(int position);

    //endregion

    @Override
    public void onBindViewHolder(DataBoundViewHolder<T> holder, int position, List<Object> payloads) {
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            bindItem(holder, position, payloads);
        }

        holder.binding.executePendingBindings();
    }

    //region methods

    protected abstract void bindItem(DataBoundViewHolder<T> holder, int position,
                                     List<Object> payloads);

    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }

    //endregion

}
