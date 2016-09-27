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
