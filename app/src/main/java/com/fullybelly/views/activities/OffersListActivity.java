package com.fullybelly.views.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.ActivityOffersListBinding;
import com.fullybelly.services.background.LocationService;
import com.fullybelly.viewModels.activities.OffersListViewModel;
import com.fullybelly.views.adapters.OffersAdapter;
import com.fullybelly.views.animation.ShowHideAnimation;
import com.fullybelly.views.utils.EndlessRecyclerViewScrollListener;

import javax.inject.Inject;

public class OffersListActivity extends BaseActivity {

    //region public fields

    @Inject
    public OffersListViewModel dataContext;

    //endregion

    //region private fields

    private LocationService mLocationService;

    private LocationServiceConnection mServiceConnection;

    private SearchDistHandler mSearchDistHandler;

    private ViewTreeObserver mObserver;

    private LayoutChangeListener mLayoutChangeListener;

    private EndlessRecyclerViewScrollListener mScrollListener;

    private boolean mSearcherExpanded;

    //endregion

    //region public methods

    public void requestSearch() {
        View view = getCurrentFocus();

        if (view != null) {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        mScrollListener.resetState();
        dataContext.search();
    }

    public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mScrollListener.resetState();
            dataContext.search();
        }

        return false;
    }

    public void animateSearcher() {
        View view = findViewById(R.id.advancedSearcherContainer);

        if (mSearcherExpanded) {
            ShowHideAnimation.hide(view);

            mSearcherExpanded = false;
        } else {
            ShowHideAnimation.show(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            mSearcherExpanded = true;
        }
    }

    //endregion

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOffersListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_offers_list);

        FullyBellyApplication.component().inject(this);
        setupRecyclerView();
        binding.setParent(this);
        binding.setDataContext(dataContext);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataContext == null) {
            FullyBellyApplication.component().inject(this);
        }

        dataContext.init();
        initEvents();
        startLocation();
    }

    @Override
    protected void onPause() {
        Intent intent = new Intent(this, LocationService.class);

        stopService(intent);
        dataContext.sleep();
        dataContext.removeOnPropertyChangedCallback(mSearchDistHandler);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dataContext.kill();

        dataContext = null;
        mSearchDistHandler = null;
        mLayoutChangeListener = null;

        super.onDestroy();
    }

    //endregion

    //region private methods

    private void startLocation() {
        Intent intent = new Intent(this, LocationService.class);
        mServiceConnection = new LocationServiceConnection();

        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupRecyclerView() {
        View view = findViewById(R.id.offersList);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    dataContext.loadNextPage(page);
                }
            };

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new OffersAdapter(dataContext, context));
            recyclerView.addOnScrollListener(mScrollListener);
        }
    }

    private void initEvents() {
        mObserver = findViewById(R.id.searcherAdvancedDistance).getViewTreeObserver();
        mLayoutChangeListener = new LayoutChangeListener();
        mSearchDistHandler = new SearchDistHandler();

        findViewById(R.id.searchTermBox).setOnFocusChangeListener(onSearchTermFocus());
        mObserver.addOnGlobalLayoutListener(mLayoutChangeListener);
        dataContext.addOnPropertyChangedCallback(mSearchDistHandler);
    }

    private View.OnFocusChangeListener onSearchTermFocus() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dataContext.hideHintText();
                } else {
                    dataContext.showHintText();
                }
            }
        };
    }

    private int getDistanceTitleLeftOffset() {
        int textWidth = (int)((TextView)findViewById(R.id.distanceBarTitle)).getPaint()
                .measureText(dataContext.distanceValue.get());

        return findViewById(R.id.distanceBar).getWidth() - textWidth;
    }

    //endregion

    //region classes

    private class SearchDistHandler extends Observable.OnPropertyChangedCallback {
        private int mMarginsInPixels;

        SearchDistHandler() {
            mMarginsInPixels = (int)getResources().getDimension(R.dimen.defaultBigMargin);
        }

        @Override
        public void onPropertyChanged(Observable object, int propertyId) {
            if (propertyId != BR.searchDist) {
                return;
            }

            dataContext.updateDistanceValue();

            Rect bounds = ((SeekBar)findViewById(R.id.distanceBar)).getThumb().getBounds();
            int left;
            int distance = dataContext.getSearchDist();

            if (distance < dataContext.getMaxDistance() && distance > 0) {
                left = bounds.left;

                if (distance >= 47) {
                    left -= mMarginsInPixels;
                }
            } else {
                left = getDistanceTitleLeftOffset();
            }

            findViewById(R.id.distanceBarTitle).animate().translationX(left);
        }
    }

    private class LayoutChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            Rect bounds = ((SeekBar)findViewById(R.id.distanceBar)).getThumb().getBounds();
            int distance = dataContext.getSearchDist();
            int left;

            if (bounds.left < 0) {
                return;
            }
            if (distance == dataContext.getMaxDistance()) {
                left = getDistanceTitleLeftOffset();
            } else {
                left = bounds.left;
            }

            findViewById(R.id.distanceBarTitle).animate().translationX(left);
            findViewById(R.id.searcherAdvancedDistance).getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    private class LocationServiceConnection implements ServiceConnection {

        //region public methods

        @Override
        public void onServiceDisconnected(ComponentName name) { mLocationService.stopListening(); }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationService = ((LocationService.LocationSvcBinder) service).getService();

            FullyBellyApplication.component().inject(mLocationService);
            mLocationService.startListening();
        }

        //endregion

    }

    //endregion

}
