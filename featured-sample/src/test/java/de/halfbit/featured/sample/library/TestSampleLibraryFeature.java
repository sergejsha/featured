package de.halfbit.featured.sample.library;

public class TestSampleLibraryFeature extends SampleLibraryFeature {

    private EventCollector mEventCollector = new EventCollector();

    @Override protected void onCreate() {
        mEventCollector.onEvent("onCreate");
    }

    @Override protected void onStart() {
        mEventCollector.onEvent("onStart");
    }

    @Override protected void onStop() {
        mEventCollector.onEvent("onStop");
    }

    @Override protected void onDestroy() {
        mEventCollector.onEvent("onDestroy");
    }

    @Override public void onDataLoaded() {
        mEventCollector.onEvent("onDataLoaded");
    }

    @Override public void onUiShown() {
        mEventCollector.onEvent("onUiShown");
    }

    public void assertEvents(String... events) {
        mEventCollector.assertEvents(events);
    }
}
