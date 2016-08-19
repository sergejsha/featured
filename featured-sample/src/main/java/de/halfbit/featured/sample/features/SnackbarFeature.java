package de.halfbit.featured.sample.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import de.halfbit.featured.sample.R;
import de.halfbit.featured.sample.SampleFeature;
import de.halfbit.featured.sample.util.Utils;

public class SnackbarFeature extends SampleFeature implements View.OnClickListener {

    private CoordinatorLayout mParent;
    private View mButton;

    @Override
    protected void onCreate(@NonNull CoordinatorLayout parent,
                            @Nullable Bundle savedInstanceState) {
        mParent = parent;
        mButton = Utils.findAndShowView(mParent, R.id.snackbar, this);
    }

    @Override protected void onFabClicked() {
        Snackbar.make(mParent,
                "Hello from SnackBarFeature via FAB click", Snackbar.LENGTH_LONG).show();
    }

    @Override protected void onDestroy() {
        mButton.setOnClickListener(null);
    }

    @Override public void onClick(View view) {
        Snackbar.make(mParent, "Hello from SnackBarFeature", Snackbar.LENGTH_LONG).show();
    }

}
