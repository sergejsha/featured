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
package de.halfbit.featured.sample.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import de.halfbit.featured.sample.SampleFeature;

public class LoggerFeature extends SampleFeature {

    private static final String TAG = "featured-sample-logger";

    @Override
    protected void onCreate(@NonNull CoordinatorLayout parent,
                            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
    }

    @Override protected void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override protected void onStop() {
        Log.d(TAG, "onStop");
    }

    @Override protected void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

}
