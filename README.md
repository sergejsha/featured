This project is **@deprecated** in favor of [Knot](https://github.com/beworker/knot) library. It offers the same capability of decomposing complex application logic into smaller features, but does it in a reactive way using modern predictable state container pattern.

---

[![Build Status](https://travis-ci.org/beworker/featured.svg?branch=master)](https://travis-ci.org/beworker/featured)
[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/featured.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.halfbit%22%20a%3A%22featured%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)


# Featured
This library will help you to split activity or fragment code into truly decoupled, testable and maintainable features.

Features are small units sharing same lifecycle. They are hosted by a `FeatureHost` class which, in turn, resides in an activity or a fragment. Features communicate to each other through the feature host by means of events as shown in the diagram below.

![diagram][1]

# Documentation

- [Quick Start][2]
- [Featured Design][3]
- [Migrate to version 0.1.0 or higher][4]

# Use with Gradle

Add this to you project-level `build.gradle`:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
}
```

Add this to your module-level `build.gradle`:

```groovy
dependencies {
    annotationProcessor 'de.halfbit:featured-compiler:<version>'
    compile 'de.halfbit:featured:<version>'
}
```

[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/featured.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.halfbit%22%20a%3A%22featured%22)

# License
```
Copyright 2016-2019 Sergej Shafarenka, www.halfbit.de

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
[4]: docs/migrate-to-0.1.0.md
