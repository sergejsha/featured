# Migrate to version 0.1.0

Main API change in this release is the additional parameter required by `Feature` class. This is a type of context your feature expects. Before the version 0.1.0 you could access context inside a feature through the feature host by calling `getFeatureHost().getContext()` methods. Now the context type belongs to feature contract and, as a result, shall be parametrized and accessed through feature class.

# Code changes

1. Add context type parameter to feature class.

 For example you had a feature like this.
 ```
 public class MyFeature extends Feature<MyFeatureHost> {
 }
 ```

 Just add a context type parameter as following.
 ```
 public class MyFeature extends Feature<MyFeatureHost, Context> {
 }
 ```

 You can use any class as a context. Typical types of choice are `Context` or `Activity`, but you can use any type you want.

2. Replace `getFeatureHost().getContext()` with `getContext()`

3. Build -> Clean Project

4. Build -> Rebuild Project

Your code must compile and run again.
