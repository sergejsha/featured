# Featured
helps you to split activity or fragment code into truly decoupled, testable and maintainable features.

Features are hosted by a `FeatureHost` which resides in an activity or a fragment. Features communicate to each other through the feature host by means of events as shown in the picture below.

![diagram][1]

# How to start with featured desing?

1) First you need to think of splitting your activity into independent components communicating via events. Start with standard lifecycle events like `onCreate()`, `onStart()` etc. and add your events to them.

2) Create basis feature class and add all events of all possible components, including standard lifecycle events.
```java
public class SampleFeature extends Feature<SampleFeatureHost> {
    @FeatureEvent protected void onCreate(@NonNull CoordinatorLayout parent) {}
    @FeatureEvent protected void onStart() {}
    @FeatureEvent protected void onFabClicked() {}
    @FeatureEvent protected void onStop() {}
    @FeatureEvent protected void onDestroy() {}
}
```
Because your feature is called `SampleFeature`, generated feature host class will be called `SampleFeatureHost`. You need to use this name as parameter of extended `Feature` class. Save and build `SampleFeature` class. Feature host will be generated and sources will compile successfully.

3) Now implement your features by extending basis `SampleFeature` class.
```java
public class ToolbarFeature extends SampleFeature {
    @Override protected void onFabClicked() {
        // handle on-click-event here
    }
}

public class FabFeature extends SampleFeature implements View.OnClickListener {
    @Override public void onClick(View view) {
        // dispatch on-click-event to other features
        getFeatureHost().dispatchOnFabClicked();
    }
}
```
Generated feature host class contains dispatch-methods for each event method of `SampleFeature`. Use these methods to dispatch events to all features. You can call those methods from activity or from inside any feature.

4) Register features to the feature host class in your activity or a fragment.
```java
public MyFragment extends Fragment {
    private SampleFeatureHost mFeatureHost;
    
    @Override public void onCreate(Bundle savedInstanceState) {
      ...
      CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.coordinator);
      
      // create feature host and add a feature we created
      mFeatureHost = new SampleFeatureHost(getContext())
            .with(new FabFeature())
            .with(new ToolbarFeature());
            
      // dispatch event to all registered features
      mFeatureHost.dispatchOnCreate(parent);
    }
    
    // dispatch other events correspondingly
    @Override public void onStart() { mFeatureHost.dispatchOnStart(); }
    @Override public void onStop() { mFeatureHost.dispatchOnStop(); }
    @Override public void onDestroy() { mFeatureHost.dispatchOnDestroy(); }
}
```
Your fragment become very simple and whole application code gets split into separated features with very clean responcibility. You can add new features or disable existing features depending on the device configuration etc. See `featured-sample` project for more detail.

# Use with Gradle

Add this to you project-level `build.gradle`:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}
```

Add this to your module-level `build.gradle`:

```groovy
apply plugin: 'android-apt'

android {
    ...
}

dependencies {
    apt "de.halfbit:featured-compiler:0.0.2"
    compile "de.halfbit:featured:0.0.2"
}
```

# Featured design
Here is some rules and implementaiton details helping you to become familiar with the library and write cleaner code.
- Features must not access other features directly but shall always interoperate through feature callbacks. By following this rule you will be rewarded with simple, maintainable and testable code later on.
- You can define as many event callbacks as you need and they can have as many parameters as you need.
- Generated feature host class will contain a `dispatchOn<Event>()` method for each feature's `on<Event>()` method.
- To dispatch an event to all features you just need to call its corresponding `dispatchOn<Event>()`.
- It is allowed to call a `dispatchOn<AnotherEvent>()` method from a feature's `on<Event>()` callback. Feature host will make sure that currently running dispatch loop finishes and current event gets dispatched to all features before the new event gets dispatched.
- This make event dispatching to be asynchronous. It means you cannot assume that a `dispatchOn<Event>()` finishes, corresponding event has been delivered to all features. Actual event dispatching can happen also later in time. If you want to be notified after an event has been dispatched, you need to use `@FeatureEvent(dispatchCompleted = true)` and provide corresponding `OnDispatchCompleted` callback in `dispatchOn<Event>()` method. Provided callback will be notified after event dispatching finishes.
- Current implementation is intended to be used in MainThread. This is the only thread strategy implemented at this time.
- Featured is being actively developed and new library features are to be expected.

# License
```
Copyright 2016 Sergej Shafarenka, www.halfbit.de

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: web/diagram.png
