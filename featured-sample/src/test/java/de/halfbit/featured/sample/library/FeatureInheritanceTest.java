package de.halfbit.featured.sample.library;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FeatureInheritanceTest {

    @Test
    public void testEventDispatching() {

        TestLifecycleFeature testLifecycleFeature = new TestLifecycleFeature();
        TestSampleLibraryFeature testSampleLibraryFeature = new TestSampleLibraryFeature();

        SampleLibraryFeatureHost host = new SampleLibraryFeatureHost(new SampleLibraryActivity())
                .with(testLifecycleFeature)
                .with(testSampleLibraryFeature);

        host.dispatchOnCreate();
        host.dispatchOnStart();
        host.dispatchOnDataLoaded();
        host.dispatchOnUiShown();
        host.dispatchOnStop();
        host.dispatchOnDestroy();

        testLifecycleFeature.assertEvents(
                "onCreate", "onStart", "onStop", "onDestroy");

        testSampleLibraryFeature.assertEvents(
                "onCreate", "onStart", "onDataLoaded", "onUiShown", "onStop", "onDestroy");
    }

}
