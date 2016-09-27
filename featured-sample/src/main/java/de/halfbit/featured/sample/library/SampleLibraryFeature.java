package de.halfbit.featured.sample.library;

import android.app.Activity;

import de.halfbit.featured.FeatureEvent;

/**
 * This application feature extends a library feature and inherits its whole lifecycle.
 * Generated feature host will include dispatch methods for {@code SampleLibraryFeature}
 * as well as {@code LifecycleFeature} events.
 */
public class SampleLibraryFeature extends LifecycleFeature<SampleLibraryFeatureHost, Activity> {

    @FeatureEvent
    public void onDataLoaded() {
        // nop
    }

    @FeatureEvent
    public void onUiShown() {
        // nop
    }

}
