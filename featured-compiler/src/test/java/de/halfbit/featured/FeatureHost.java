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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// Dummy class for tests to compile
public abstract class FeatureHost<FH extends FeatureHost, C> {

    public interface OnDispatchCompleted {
        void onDispatchCompleted();
    }

    protected static abstract class Event {
        @Nullable
        protected OnDispatchCompleted mOnDispatchCompleted;

        protected abstract void dispatch(@NonNull Feature feature);
    }

    public FeatureHost(@NonNull C context) {
        throw new RuntimeException("Stub!");
    }

    protected void dispatch(Event event) {
        throw new RuntimeException("Stub!");
    }

    protected void addFeature(Feature feature) {
        throw new RuntimeException("Stub!");
    }

}
