package de.halfbit.featured.sample.library;

import de.halfbit.featured.Feature;
import de.halfbit.featured.FeatureEvent;

/**
 * This is a base lifecycle feature defined in a library. It can be used
 * in multiple project. Just import the library and extend this feature
 * to get access to its callbacks.
 */
public class LifecycleFeature extends Feature<LifecycleFeatureHost> {

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
