package de.halfbit.featured.sample.library;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * This is just a demo activity showing how you can combine features inheriting same
 * lifecycle in a single feature host. Feature host is capable to dispatch all events.
 * Each registered feature will only receive events belonging to its lifecycle.
 */
public class SampleLibraryActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        SampleLibraryFeatureHost host = new SampleLibraryFeatureHost(this)
                .with(new LifecycleFeature())
                .with(new SampleLibraryFeature());

        host.dispatchOnCreate();
        host.dispatchOnDataLoaded();
    }

}
