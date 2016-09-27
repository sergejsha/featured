# Featured
This library will help you to split activity or fragment code into truly decoupled, testable and maintainable features.

Features are small units sharing same lifecycle. They are hosted by a `FeatureHost` class which, in turn, resides in an activity or a fragment. Features communicate to each other through the feature host by means of events as shown in the diagram below.

![diagram][1]

# Documentation

- [Quick Start][2]
- [Featured Design][3]

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
    apt "de.halfbit:featured-compiler:0.1.0"
    compile "de.halfbit:featured:0.1.0"
}
```

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

[1]: docs/images/diagram.png
[2]: docs/quick-start.md
[3]: docs/featured-design.md
