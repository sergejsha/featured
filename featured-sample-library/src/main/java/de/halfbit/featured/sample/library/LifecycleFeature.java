package de.halfbit.featured.sample.library;

import android.app.Activity;

import de.halfbit.featured.Feature;
import de.halfbit.featured.FeatureEvent;

/**
 * This is a base lifecycle feature defined in a library. It can be used
 * in other project. Just import the library and extend this feature
 * to get access to its callbacks. See how it is done in {@code SampleLibraryFeature}
 * in featured-sample project.
 */
public class LifecycleFeature<FH extends LifecycleFeatureHost, C extends Activity> extends Feature<FH, C> {

    @FeatureEvent
    protected void onCreate() {
        // nop
    }

    @FeatureEvent
    protected void onStart() {
        // nop
    }

    @FeatureEvent
    protected void onStop() {
        // nop
    }

    @FeatureEvent
    protected void onDestroy() {
        // nop
    }

}
