package com.fullybelly.views.adapters;

import android.content.Context;
import android.databinding.ObservableList;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentOffersListItemBinding;
import com.fullybelly.viewModels.activities.OffersListViewModel;
import com.fullybelly.viewModels.fragments.OffersListItemViewModel;
import com.fullybelly.views.adapters.base.DataBoundAdapter;

import java.util.List;

public class OffersAdapter extends DataBoundAdapter<FragmentOffersListItemBinding> {

    //region fields

    private OffersListViewModel mDataContext;

    private final Context mParentView;

    private ObservableList.OnListChangedCallback<ObservableList<OffersListItemViewModel>> mCallback = new ObservableList.OnListChangedCallback<ObservableList<OffersListItemViewModel>>() {
        @Override
        public void onChanged(ObservableList<OffersListItemViewModel> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<OffersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<OffersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<OffersListItemViewModel> sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemRangeRemoved(fromPosition, itemCount);
            notifyItemRangeInserted(toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<OffersListItemViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);

            for (OffersListItemViewModel itemViewModel : sender) {
                itemViewModel.sleep();
            }
        }
    };

    //endregion

    //region constructor

    public OffersAdapter(OffersListViewModel dataContext, Context parentView) {
        super(R.layout.fragment_offers_list_item);

        mDataContext = dataContext;
        mParentView = parentView;

        mDataContext.getOffers().addOnListChangedCallback(mCallback);
    }

    //endregion

    //region public methods

    @Override
    protected void bindItem(DataBoundViewHolder<FragmentOffersListItemBinding> holder, int position, List<Object> payloads) {
        holder.binding.setOffer(mDataContext.getOffer(position));
    }

    @Override
    public int getItemCount() {
        return mDataContext.getOffers().size();
    }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentOffersListItemBinding> holder, int position) { }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentOffersListItemBinding> holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        OffersListItemViewModel vm = mDataContext.getOffers().get(position);
        ImageView imageView = (ImageView)holder.itemView.findViewById(R.id.restaurantLogo);

        if (vm.getLogoUrl() != null) {
            String url = vm.getLogoUrl();

            Glide.with(mParentView)
                 .load(url)
                 .into(imageView);
        } else {
            Glide.clear(imageView);
            imageView.setImageDrawable(null);
        }

        TextView tv = (TextView)holder.itemView.findViewById(R.id.priceOld);

        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    //endregion

}