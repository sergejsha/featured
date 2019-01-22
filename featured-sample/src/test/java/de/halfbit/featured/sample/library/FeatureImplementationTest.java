package de.halfbit.featured.sample.library;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class FeatureImplementationTest {

    @Test
    public void testEventDispatchingWithInheritance() {

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

    @Test
    public void testNamedFeatures() {

        TestLifecycleFeature testLifecycleFeature1 = new TestLifecycleFeature();
        TestLifecycleFeature testLifecycleFeature2 = new TestLifecycleFeature();

        SampleLibraryFeatureHost host = new SampleLibraryFeatureHost(new SampleLibraryActivity())
                .with(testLifecycleFeature1)
                .with(testLifecycleFeature1, "feature1")
                .with(testLifecycleFeature2, "feature2");

        host.dispatchOnCreate();
        host.dispatchOnDestroy();

        TestLifecycleFeature feature1a = host.getFeature(TestLifecycleFeature.class);
        assertThat(feature1a).isSameAs(testLifecycleFeature1);

        TestLifecycleFeature feature1b = host.getFeature(TestLifecycleFeature.class, "feature1");
        assertThat(feature1b).isSameAs(testLifecycleFeature1);

        TestLifecycleFeature feature2b = host.getFeature(TestLifecycleFeature.class, "feature2");
        assertThat(feature2b).isSameAs(testLifecycleFeature2);

        testLifecycleFeature1.assertEvents("onCreate", "onCreate", "onDestroy", "onDestroy");
        testLifecycleFeature2.assertEvents("onCreate", "onDestroy");
    }

}
