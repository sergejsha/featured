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

import android.app.Activity;
import android.os.Bundle;

import de.halfbit.featured.sample.features.LoggerFeature;

public class SampleActivity extends Activity {

    private ActivityFeatureHost mFeatureHost;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFeatureHost = new ActivityFeatureHost(this)
                .addFeature(new LoggerFeature());

        mFeatureHost.dispatchOnCreate(savedInstanceState);
    }

    @Override protected void onStart() {
        super.onStart();
        mFeatureHost.dispatchOnStart();
    }

    @Override protected void onStop() {
        mFeatureHost.dispatchOnStop();
        super.onStop();
    }

    @Override protected void onDestroy() {
        mFeatureHost.dispatchOnDestroy();
        super.onDestroy();
    }

}
