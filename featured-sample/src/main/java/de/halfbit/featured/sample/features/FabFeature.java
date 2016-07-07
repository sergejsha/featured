package de.halfbit.featured.sample.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import de.halfbit.featured.sample.R;
import de.halfbit.featured.sample.SampleFeature;
import de.halfbit.featured.sample.util.Utils;

public class FabFeature extends SampleFeature implements View.OnClickListener {

    private FloatingActionButton mButton;

    @Override
    protected void onCreate(@NonNull CoordinatorLayout parent, @Nullable Bundle savedInstanceState) {
        mButton = Utils.findAndShowView(parent, R.id.fab, this);
    }

    @Override public void onClick(View view) {
        getFeatureHost().dispatchOnFabClicked();
    }

}
