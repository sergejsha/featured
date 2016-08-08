package de.halfbit.featured.sample.library;

import de.halfbit.featured.Feature;
import de.halfbit.featured.FeatureEvent;

/**
 * This is a base lifecycle feature defined in a library. It can be used
 * in other project. Just import the library and extend this feature
 * to get access to its callbacks. See how it is done in {@code SampleLibraryFeature}
 * in featured-sample.
 */
public class LifecycleFeature<FH extends LifecycleFeatureHost> extends Feature<FH> {

    @FeatureEvent
    public void onCreate() {
        // nop
    }

    @FeatureEvent
    public void onStart() {
        // nop
    }

    @FeatureEvent
    public void onStop() {
        // nop
    }

    @FeatureEvent
    public void onDestroy() {
        // nop
    }

}
