package de.halfbit.featured.sample.library;

public class TestLifecycleFeature extends LifecycleFeature {

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

    public void assertEvents(String... events) {
        mEventCollector.assertEvents(events);
    }
}
