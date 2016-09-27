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