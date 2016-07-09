# Featured
splits your activity or fragment code into truly decoupled, testable and maintainable features.

Features are hosted by a `FeatureHost` which resides in an activity or a fragment. Features communicate to each other through the feature host by means of events as shown in the picture below.

![diagram][1]

# How do I write a feature?

1) Create a basis class for your features with all events they can receive.

```java
public class SampleFeature extends Feature<SampleFeatureHost> {

    @FeatureEvent protected void onCreate(@NonNull CoordinatorLayout parent) {}
    @FeatureEvent protected void onStart() {}
    @FeatureEvent protected void onFabClicked() {}
    @FeatureEvent protected void onStop() {}
    @FeatureEvent protected void onDestroy() {}
    
}
```
`SampleFeatureHost` will be generated for you as soon as you save and build your feature class. It contains corresponding dispatch-methods and will host all your features.

2) Implement your features.

```java
public class ToolbarFeature extends SampleFeature {
    private Toolbar mToolbar;

    @Override protected void onCreate(@NonNull CoordinatorLayout parent) {
        mToolbar = Utils.findAndShowView(parent, R.id.toolbar);
        AppCompatActivity activity = Utils.getActivity(parent.getContext());
        activity.setSupportActionBar(mToolbar);
    }
    
    @Override protected void onFabClicked() {
        // write your FAB handler in here
    }
}

public class FabFeature extends SampleFeature implements View.OnClickListener {
    private FloatingActionButton mButton;

    @Override protected void onCreate(@NonNull CoordinatorLayout parent) {
        mButton = Utils.findAndShowView(parent, R.id.fab, this);
    }

    @Override public void onClick(View view) {
        // dispatch click to other features
        getFeatureHost().dispatchOnFabClicked();
    }
}
```

3) Add features to the feature host class in your activity or a fragment.

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

Your fragment become very simple and whole application code gets split into separated features with very clean responcibility. See featured-sample project for more details.

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
    apt "de.halfbit:featured-compiler:0.0.1"
    compile "de.halfbit:featured:0.0.1"
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
- Features is being actively developed and new library features are to be expected.

[1]: web/diagram.png
