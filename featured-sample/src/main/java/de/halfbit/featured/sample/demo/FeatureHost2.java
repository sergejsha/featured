package de.halfbit.featured.sample.demo;

import android.support.v4.util.SimpleArrayMap;

public class FeatureHost2 {

    private static SimpleArrayMap<Class, Integer> sFeatureTypeIndex;

    static {
        sFeatureTypeIndex.put(Feature1.class, 1);
        sFeatureTypeIndex.put(Feature2.class, 2);
    }

    public void dispatchOnStart() {
        Object feature = new Object();
        Integer typeIndex = sFeatureTypeIndex.get(feature.getClass());
        new OnStartEvent().dispatch(feature, typeIndex);
    }

    public void dispatchOnStop() {


    }

    public static class OnStartEvent {

        public void dispatch(Object feature, int typeIndex) {
            switch (typeIndex) {
                case 1: ((Feature1) feature).onStart(); break;
                case 2: ((Feature2) feature).onStart(); break;
            }
        }

    }

}
