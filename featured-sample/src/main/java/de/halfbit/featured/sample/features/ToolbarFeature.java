package de.halfbit.featured.sample.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.halfbit.featured.sample.R;
import de.halfbit.featured.sample.SampleFeature;
import de.halfbit.featured.sample.util.Utils;

public class ToolbarFeature extends SampleFeature {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@NonNull CoordinatorLayout parent,
                            @Nullable Bundle savedInstanceState) {
        mToolbar = Utils.findAndShowView(parent, R.id.toolbar);

        AppCompatActivity activity = Utils.getActivity(parent.getContext());
        activity.setSupportActionBar(mToolbar);
    }

}
