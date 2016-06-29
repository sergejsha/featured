# Featured
It structures activity's or fragment's code into small, decoupled, maintainable and testable units called features. 

Features are hosted by a `FeatureHost` which resides in an activity or a fragment. Features communicate to each other through their feature host by means of events as shown in the picture below.

![diagram][1]

# How can I write a feature?

```java

// 1. Create a base feature class with all events your features will exchange.

public class FragmentFeature extends Feature<FragmentFeatureHost> {
    @FeatureEvent protected void onCreate(Bundle savedInstanceState) {}
    @FeatureEvent protected void onStart() {}
    @FeatureEvent protected void onStop() {}
    @FeatureEvent protected void onDestroy() {}
}

// We defined standard fragments lifecycle events for sake of simplicity. 
// In your real app the events can be anything you need.

// 2. Now you can start writing your features by extending just created `FragmentFeature` class.

public class LoggerFeature extends ActivityFeature {
    private static final String TAG = "sample-app";

    @Override protected void onCreate(Bundle savedInstanceState) { Log.d(TAG, "onCreate"); }
    @Override protected void onStart() { Log.d(TAG, "onStart"); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); }
}

// 3. Last step is to create a feature host and to settle your features there.

public MyVeryComplexFragment extends Fragment {
    private FragmentFeatureHost mFeatureHost;
    
    @Override public void onCreate(Bundle savedInstanceState) {
    
      // create feature host and add a feature we created
      mFeatureHost = new FragmentFeatureHost(getContext())
            .addFeature(new LoggerFeature());
            
      // dispatch event to all registered features
      mFeatureHost.dispatchOnCreate(savedInstanceState);
    }
    
    // dispatch other events correspondingly
    @Override public void onStart() { mFeatureHost.dispatchOnStart(); }
    @Override public void onStop() { mFeatureHost.dispatchOnStop(); }
    @Override public void onDestroy() { mFeatureHost.dispatchOnDestroy(); }
}

// That's it.
```
Now your complext fragment became just a barebone for your features and the whole complext logic is grouped by its aspects into individual features.

You might ask where the `FragmentFeatureHost` class come from? Featured library parses `@FeatureEvent` annotations in the basefeature class and generates a proper feature host class for you. Every change in `FragmentFeature` will be reflected in `FragmentFeatureHost` after rebuilding the project.

# I need more
Here is some rules and implementaiton details helping you to become familiar with the library and write cleaner code.
- Features must not access other features directly but shall interoperate through feature callbacks. Following this rule will be rewarded by simple, interchangible, maintainable and testable code later on.
- You can define as many event callbacks as you need and event callbacks can have as many parameters as you need.
- Generated feature host class contains a `dispatchOnEvent()` method for each `onEvent()` method.
- To dispatch an event to all features you just need to call a corresponding `dispatchOnEvent()`.
- It is allowed to call another `dispatchOnEvent()` method from a feature's `onEvent()` callback. Feature host will make sure that currently running dispatch loop finishes and current event gets dispatched to all features, and only then the new event will be dispatched.
- This make event dispatching to be asynchronous. It means you cannot assume that after calling a `dispatchOnEvent()` method, corresponding event has been delivered to all features. This can happen also later in time. If you want to be notified after an event has been dispatched, you need to use `@FeatureEvent(dispatchCompleted = true)` and provide corresponding `OnDispatchCompleted` callback when calling `dispatchOnEvent()` method.
- Current implementation is not thread safe and is intended to be used in MainThread.

[1]: web/diagram.png
