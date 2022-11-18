package com.fullybelly.views.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.CallbackManager;
import com.fullybelly.FullyBellyApplication;
import com.fullybelly.R;
import com.fullybelly.databinding.ActivityLoginBinding;
import com.fullybelly.viewModels.activities.LoginViewModel;
import com.fullybelly.views.adapters.PartnersAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity {

    //region public fields

    @Inject
    public LoginViewModel dataContext;

    //endregion

    //region private fields

    private CallbackManager mCallbackManager;

    //endregion

    //region lifecycle events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        FullyBellyApplication.component().inject(this);
        binding.setDataContext(dataContext);
        binding.setParent(this);

        mCallbackManager = CallbackManager.Factory.create();

        View row1Partners = findViewById(R.id.partnersListRow1);
        View row2Partners = findViewById(R.id.partnersListRow2);
        ArrayList<View> rows = new ArrayList<>();

        rows.add(row1Partners);
        rows.add(row2Partners);

        // Set the adapters
        setPartnersAdapters(rows);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataContext == null) {
            FullyBellyApplication.component().inject(this);
        }

        dataContext.init();
    }

    @Override
    protected void onPause() {
        dataContext.sleep();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        dataContext.kill();

        dataContext = null;

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //endregion

    //region private methods

    private void setPartnersAdapters(ArrayList<View> rows) {
        for (int i = 0; i < rows.size(); i++) {
            View view = rows.get(i);

            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new PartnersAdapter(dataContext, context, i));
            }
        }
    }

    //endregion

}
