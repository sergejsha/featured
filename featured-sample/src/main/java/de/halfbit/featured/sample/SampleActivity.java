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
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import de.halfbit.featured.sample.features.FabFeature;
import de.halfbit.featured.sample.features.LoggerFeature;
import de.halfbit.featured.sample.features.SnackbarFeature;
import de.halfbit.featured.sample.features.ToastFeature;
import de.halfbit.featured.sample.features.ToolbarFeature;

public class SampleActivity extends AppCompatActivity {

    private SampleFeatureHost mFeatureHost;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // try to comment out some features and see what happens
        mFeatureHost = new SampleFeatureHost(this)
                .with(new LoggerFeature())
                .with(new ToolbarFeature())
                .with(new SnackbarFeature())
                .with(new ToastFeature())
                .with(new FabFeature());

        CoordinatorLayout parent = (CoordinatorLayout)
                findViewById(R.id.coordinatorLayout);
        mFeatureHost.dispatchOnCreate(parent, savedInstanceState);
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
