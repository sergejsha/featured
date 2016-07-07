# Featured
It easily structures activity's or fragment's code into small, decoupled, maintainable and testable units called features. 

Features are hosted by a `FeatureHost` which resides in an activity or a fragment. Features communicate to each other through their feature host by means of events as shown in the picture below.

![diagram][1]

# How can I write a feature?


1) Create a basis class for yout features. It declares all events every feature can receive.

```java
public class SampleFeature extends Feature<SampleFeatureHost> {

    @FeatureEvent protected void onCreate(@NonNull CoordinatorLayout parent, 
                                          @Nullable Bundle savedInstanceState) {}
    @FeatureEvent protected void onStart() {}
    @FeatureEvent protected void onFabClicked() {}
    @FeatureEvent protected void onStop() {}
    @FeatureEvent protected void onDestroy() {}
    
}
```

2) Start writing your features by extending `SampleFeature` class.

```java
public class ToolbarFeature extends SampleFeature {
    private Toolbar mToolbar;

    @Override protected void onCreate(@NonNull CoordinatorLayout parent, 
                                      @Nullable Bundle savedInstanceState) {
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

    @Override protected void onCreate(@NonNull CoordinatorLayout parent, 
                                      @Nullable Bundle savedInstanceState) {
        mButton = Utils.findAndShowView(parent, R.id.fab, this);
    }

    @Override public void onClick(View view) {
        getFeatureHost().dispatchOnFabClicked();
    }
}
```

3) Add features to feature host class in your activity or a fragment.

```java
public MyFragment extends Fragment {
    private SampleFeatureHost mFeatureHost;
    
    @Override public void onCreate(Bundle savedInstanceState) {
    
      // create feature host and add a feature we created
      mFeatureHost = new SampleFeatureHost(getContext())
            .with(new FabFeature())
            .with(new ToolbarFeature());
            
      // dispatch event to all registered features
      mFeatureHost.dispatchOnCreate(savedInstanceState);
    }
    
    // dispatch other events correspondingly
    @Override public void onStart() { mFeatureHost.dispatchOnStart(); }
    @Override public void onStop() { mFeatureHost.dispatchOnStop(); }
    @Override public void onDestroy() { mFeatureHost.dispatchOnDestroy(); }
}
```

Your fragment become very simple and whole application code gets split into separated features with very clean responcibility. See Sample App for more details

Where did the `SampleFeatureHost` class come from? The library parses `@FeatureEvent` annotations in the base feature class and generates a proper feature host class for you. Every change in `SampleFeature` will be reflected in `SampleFeatureHost` after rebuilding the project.

# I need more
Here is some rules and implementaiton details helping you to become familiar with the library and write cleaner code.
- Features must not access other features directly but shall interoperate through feature callbacks. By following this rule you'll be rewarded with simple, interchangible, maintainable and testable code later on.
- You can define as many event callbacks as you need and event callbacks can have as many parameters as you need.
- Generated feature host class contains a `dispatchOnEvent()` method for each `onEvent()` method.
- To dispatch an event to all features you just need to call a corresponding `dispatchOnEvent()`.
- It is allowed to call another `dispatchOnEvent()` method from a feature's `onEvent()` callback. Feature host will make sure that currently running dispatch loop finishes and current event gets dispatched to all features, and only then the new event will be dispatched.
- This make event dispatching to be asynchronous. It means you cannot assume that after calling a `dispatchOnEvent()` method, corresponding event has been delivered to all features. This can happen also later in time. If you want to be notified after an event has been dispatched, you need to use `@FeatureEvent(dispatchCompleted = true)` and provide corresponding `OnDispatchCompleted` callback when calling `dispatchOnEvent()` method.
- Current implementation is not thread safe and is intended to be used in MainThread.

[1]: web/diagram.png
