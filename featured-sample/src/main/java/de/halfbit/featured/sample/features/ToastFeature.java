package de.halfbit.featured.sample.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Toast;

import de.halfbit.featured.sample.R;
import de.halfbit.featured.sample.SampleFeature;
import de.halfbit.featured.sample.util.Utils;

public class ToastFeature extends SampleFeature implements View.OnClickListener {

    private View mButton;

    @Override
    protected void onCreate(@NonNull CoordinatorLayout parent,
                            @Nullable Bundle savedInstanceState) {
        mButton = Utils.findAndShowView(parent, R.id.toast, this);
    }

    @Override protected void onDestroy() {
        mButton.setOnClickListener(null);
    }

    @Override public void onClick(View view) {
        Toast.makeText(view.getContext(), "Hello from ToastFeature", Toast.LENGTH_LONG).show();
    }

}
