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
package de.halfbit.featured.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;

import de.halfbit.featured.Feature;
import de.halfbit.featured.FeatureEvent;

/**
 * Basis class for all features. It declares all events every feature can receive.
 */
public class SampleFeature extends Feature<SampleFeatureHost> {

    @FeatureEvent
    protected void onCreate(@NonNull CoordinatorLayout parent, @Nullable Bundle savedInstanceState) {
        // nop
    }

    @FeatureEvent
    protected void onStart() {
        // nop
    }

    @FeatureEvent
    protected void onFabClicked() {
        // nop
    }

    @FeatureEvent
    protected void onStop() {
        // nop
    }

    @FeatureEvent
    protected void onDestroy() {
        // nop
    }

}
