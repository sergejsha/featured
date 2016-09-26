/*
 * Copyright (C) 2016 Sergej Shafarenka, www.halfbit.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.halfbit.featured;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

/**
 * Base class for generated feature host classes.
 *
 * @author sergej shafarenka
 */
public abstract class FeatureHost<C> {

    /**
     * Callback interface which is called right after a feature event has been dispatched.
     */
    public interface OnDispatchCompleted {

        /**
         * Called after a feature event has just been dispatched.
         */
        void onDispatchCompleted();
    }

    protected static abstract class Event {
        @Nullable
        Event mNextEvent;

        @Nullable
        protected OnDispatchCompleted mOnDispatchCompleted;

        protected abstract void dispatch(@NonNull Feature feature);
    }

    private final C mContext;
    private final ArrayMap<Class<? extends Feature>, Feature> mFeatures;
    private Event mDispatchingEvent;

    /**
     * Creates new feature host instance and attaches given context to it.
     *
     * @param context context to be attached
     */
    public FeatureHost(@NonNull C context) {
        mFeatures = new ArrayMap<>(10);
        mContext = context;
    }

    /**
     * Returns context attached to this feature host instance.
     *
     * @return context attached to this feature host instance.
     */
    @NonNull
    protected C getContext() {
        return mContext;
    }

    /**
     * Registers a feature at the feature host.
     *
     * @param feature feature instance to be registered
     */
    @SuppressWarnings("unchecked")
    protected void addFeature(Feature feature) {
        Class<? extends Feature> clazz = feature.getClass();
        if (mFeatures.containsKey(clazz)) {
            throw new IllegalArgumentException(
                    String.format("Feature %s is already registered", clazz));
        }
        mFeatures.put(clazz, feature);
        feature.attachFeatureHost(this);
    }

    /**
     * Get a feature by its classname, which is registered in the feature host.
     *
     * @param featureClass feature class
     * @return registered feature of {@code null}
     */
    @Nullable
    public <F extends Feature> F getFeature(Class<F> featureClass) {
        //noinspection unchecked
        return (F) mFeatures.get(featureClass);
    }

    protected void dispatch(Event event) {
        assertMainThread();

        if (mDispatchingEvent == null) {
            mDispatchingEvent = event;

            // dispatch event right away
            Event e = mDispatchingEvent;
            while (e != null) {

                // dispatch to all features first
                for (int i = 0, size = mFeatures.size(); i < size; i++) {
                    //noinspection unchecked
                    e.dispatch(mFeatures.valueAt(i));
                }

                // dispatch event completion now
                if (e.mOnDispatchCompleted != null) {
                    e.mOnDispatchCompleted.onDispatchCompleted();
                }

                // pick up next event
                e = e.mNextEvent;
            }

            mDispatchingEvent = null;
            return;
        }

        // already dispatching, put event to the end of chain
        Event e = mDispatchingEvent;
        while (e.mNextEvent != null) {
            e = e.mNextEvent;
        }
        e.mNextEvent = event;
    }

    private void assertMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException(
                    "Dispatch must be called in MainThread. Current thread: "
                            + Thread.currentThread());
        }
    }

}
