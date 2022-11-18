package com.fullybelly.views.adapters;

import android.content.Context;
import android.databinding.ObservableList;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fullybelly.R;
import com.fullybelly.databinding.FragmentPartnerImageBinding;
import com.fullybelly.viewModels.activities.LoginViewModel;
import com.fullybelly.views.adapters.base.DataBoundAdapter;

import java.util.List;

public class PartnersAdapter extends DataBoundAdapter<FragmentPartnerImageBinding> {

    //region constants

    private static final int ROW_SIZE = 3;

    //endregion

    //region fields

    private LoginViewModel mDataContext;

    private final Context mParentView;

    private final int mRowNumber;

    private ObservableList.OnListChangedCallback<ObservableList<String>> mCallback = new ObservableList.OnListChangedCallback<ObservableList<String>>() {
        @Override
        public void onChanged(ObservableList<String> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemRangeRemoved(fromPosition, itemCount);
            notifyItemRangeInserted(toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    };

    //endregion

    //region constructor

    public PartnersAdapter(LoginViewModel dataContext, Context parentView, int rowNumber) {
        super(R.layout.fragment_partner_image);

        mDataContext = dataContext;
        mParentView = parentView;
        mRowNumber = rowNumber;

        mDataContext.getPartnersLogotypes().addOnListChangedCallback(mCallback);
    }

    //endregion

    //region public methods

    @Override
    protected void bindItem(DataBoundViewHolder<FragmentPartnerImageBinding> holder, int position, List<Object> payloads) {

    }

    @Override
    public int getItemCount() {
        List<String> logotypes = mDataContext.getPartnersLogotypes();

        if (logotypes.size() == 0) {
            return 0;
        }

        int fromIndex = mRowNumber * ROW_SIZE;
        int toIndex = fromIndex + ROW_SIZE;

        if (toIndex > logotypes.size()) {
            toIndex = logotypes.size();
        }
        if (toIndex < fromIndex) {
            toIndex = fromIndex;
        }

        List<String> subList = logotypes.subList(fromIndex, toIndex);

        return subList.size();
    }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentPartnerImageBinding> holder, int position) { }

    @Override
    public void onBindViewHolder(DataBoundViewHolder<FragmentPartnerImageBinding> holder, int position, List<Object> payloads) {
        int actualPosition = mRowNumber * ROW_SIZE + position;

        super.onBindViewHolder(holder, position, payloads);

        String logo = mDataContext.getPartnersLogotypes().get(actualPosition);
        ImageView imageView = (ImageView)holder.itemView.findViewById(R.id.partnerImage);

        Glide.with(mParentView)
             .load(logo)
             .into(imageView);
    }

    //endregion

}