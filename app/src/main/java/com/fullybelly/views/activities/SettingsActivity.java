package com.fullybelly.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.ActivitySettingsBinding;
import com.fullybelly.viewModels.activities.SettingsViewModel;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity {

    //region public fields

    @Inject
    public SettingsViewModel dataContext;

    //endregion

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        FullyBellyApplication.component().inject(this);
        binding.setParent(this);
        binding.setDataContext(dataContext);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataContext == null) {
            FullyBellyApplication.component().inject(this);
        }
    }

    @Override
    protected void onPause() {
        dataContext.sleep();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dataContext.kill();

        dataContext = null;

        super.onDestroy();
    }

    //endregion

    //region public methods

    public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            dataContext.save();
        }

        return false;
    }

    //endregion

}
