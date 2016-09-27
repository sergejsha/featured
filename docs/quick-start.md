# Quick Start

1) First you need to think of splitting your activity into independent components communicating via events. Start with standard activity or fragment lifecycle events like `onCreate()`, `onStart()` etc. and add other custome events like `onDataLoaded`, `onButtonClicked` etc. if required.

2) Create a basis feature class and declare all lifecycle events in there. Use `@FeatureEvent` annotation for events to be dispatched.

```java
public class SampleFeature extends Feature<SampleFeatureHost, Context> {
    @FeatureEvent protected void onCreate(@NonNull CoordinatorLayout parent) {
        // nop
    }
    
    @FeatureEvent protected void onStart() {
        // nop
    }
    
    @FeatureEvent protected void onFabClicked() {
        // nop
    }
    
    @FeatureEvent protected void onStop() {
        // nop
    }
    
    @FeatureEvent protected void onDestroy() {
        // nop
    }
}
```

The `SampleFeature` class extends a parametrized `Feature` class. First parameter is the name of the feature host class to be generated. You can freely choose the name, but adding a `Host` string to the feature name is a good choice. Second parameter is a name of the context your feature is capable to access. It can be `Context`, `Activity`, `Fragment` or any other class you find usefull for your feature implementation. Later on, when you implement features you will be able to access this context from the feature code at any time.

Build the project. `SampleFeatureHost` class will be generated and source code will compile successfully.

3) Consider the `SampleFeature` class to be an interface defining feature lifecycle. Now you can implement your features by extending `SampleFeature` class as following:

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

Generated `SampleFeatureHost` class contains dispatch methods for each event method of the `SampleFeature`. Use these methods to dispatch events to your features. You can call those methods from the activity or from inside of any feature.

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

Your fragment become very simple and whole application code gets split into separated features with the very clean responcibilities. You can add new features or disable existing features depending on your use case or the device configuration etc. See `featured-sample` project for more code examples.
