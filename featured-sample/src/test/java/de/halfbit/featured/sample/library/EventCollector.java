package de.halfbit.featured.sample.library;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class EventCollector {

    private List<Object> mEvents = new ArrayList<>(10);

    public void onEvent(Object event) {
        mEvents.add(event);
    }

    public void assertEvents(String... events) {
        assertThat(mEvents)
                .containsExactly((Object[]) events)
                .inOrder();
    }

}
