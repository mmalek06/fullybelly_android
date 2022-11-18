package com.fullybelly.views.adapters;

import android.databinding.ObservableList;

import com.fullybelly.R;
import com.fullybelly.databinding.FragmentOrdersListItemBinding;
import com.fullybelly.viewModels.fragments.OrdersListItemViewModel;
import com.fullybelly.viewModels.fragments.OrdersListViewModel;
import com.fullybelly.views.adapters.base.DataBoundAdapter;

import java.util.List;

public final class OrdersAdapter extends DataBoundAdapter<FragmentOrdersListItemBinding> {

    //region fields

    private OrdersListViewModel mDataContext;

    private ObservableList.OnListChangedCallback<ObservableList<OrdersListItemViewModel>> mCallback = new ObservableList.OnListChangedCallback<ObservableList<OrdersListItemViewModel>>() {
        @Override
        public void onChanged(ObservableList<OrdersListItemViewModel> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<OrdersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<OrdersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<OrdersListItemViewModel> sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemRangeRemoved(fromPosition, itemCount);
            notifyItemRangeInserted(toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<OrdersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);

            for (OrdersListItemViewModel itemViewModel : sender) {
                itemViewModel.sleep();
            }
        }
    };

    //endregion

    //region constructor

    public OrdersAdapter(OrdersListViewModel dataContext) {
        super(R.layout.fragment_orders_list_item);

        mDataContext = dataContext;

        mDataContext.getOrders().addOnListChangedCallback(mCallback);
    }

    //endregion

    //region public methods

    @Override
    protected void bindItem(DataBoundViewHolder<FragmentOrdersListItemBinding> holder, int position, List<Object> payloads) {
        holder.binding.setOrder(mDataContext.getOrder(position));
    }

    @Override
    public int getItemCount() {
        return mDataContext.getOrders().size();
    }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentOrdersListItemBinding> holder, int position) { }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentOrdersListItemBinding> holder, int position, List<Object> payloads) { super.onBindViewHolder(holder, position, payloads); }

    //endregion

}
