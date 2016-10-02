/*
 * Copyright (C) 2016 Sergej Shafarenka, www.halfbit.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.halfbit.featured.compiler;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@RunWith(JUnit4.class)
public class FeatureProcessorTests {

    @Test
    public void checkOnEvent() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "public class TestFeature extends Feature<TestFeatureHost, Application> {",
                        "    @FeatureEvent protected void onStart() { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Application> {",
                        "    public TestFeatureHost(@NonNull Application context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart() {",
                        "        dispatch(new OnStartEvent());",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "        if (feature instanceof TestFeature) {",
                        "            ((TestFeature) feature).onStart();",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkCustomFeatureHostName() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "public class TestFeature extends Feature<CustomTestFeatureHost, Application> {",
                        "    @FeatureEvent protected void onStart() { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.CustomTestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class CustomTestFeatureHost extends FeatureHost<CustomTestFeatureHost, Application> {",
                        "    public CustomTestFeatureHost(@NonNull Application context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public CustomTestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public CustomTestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart() {",
                        "        dispatch(new OnStartEvent());",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "        if (feature instanceof TestFeature) {",
                        "            ((TestFeature) feature).onStart();",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventWithParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent protected void onStart(long time, boolean valid, int count, Object state) { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart(long time, boolean valid, int count, Object state) {",
                        "        dispatch(new OnStartEvent(time, valid, count, state));",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        private final long mTime;",
                        "        private final boolean mValid;",
                        "        private final int mCount;",
                        "        private final Object mState;",
                        "        OnStartEvent(long time, boolean valid, int count, Object state) {",
                        "            mTime = time;",
                        "            mValid = valid;",
                        "            mCount = count;",
                        "            mState = state;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "                ((TestFeature) feature).onStart(mTime, mValid, mCount, mState);",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventWithParametersGenerics() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "import java.util.List;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent protected void onStart(List<String> names) { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "import java.util.List;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart(List<String> names) {",
                        "        dispatch(new OnStartEvent(names));",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        private final List<String> mNames;",
                        "        OnStartEvent(List<String> names) {",
                        "            mNames = names;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "                ((TestFeature) feature).onStart(mNames);",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventDispatchCompleted() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent(dispatchCompleted = true) protected void onStart() { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart(FeatureHost.OnDispatchCompleted onDispatchCompleted) {",
                        "        dispatch(new OnStartEvent(onDispatchCompleted));",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        OnStartEvent(FeatureHost.OnDispatchCompleted onDispatchCompleted) {",
                        "            mOnDispatchCompleted = onDispatchCompleted;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "               ((TestFeature) feature).onStart();",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventDispatchCompletedWithParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent(dispatchCompleted = true) protected void onStart(int time) { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart(int time, FeatureHost.OnDispatchCompleted onDispatchCompleted) {",
                        "        dispatch(new OnStartEvent(time, onDispatchCompleted));",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        private final int mTime;",
                        "        OnStartEvent(int time, FeatureHost.OnDispatchCompleted onDispatchCompleted) {",
                        "            mTime = time;",
                        "            mOnDispatchCompleted = onDispatchCompleted;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "                ((TestFeature) feature).onStart(mTime);",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventWithAnnotatedParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "import android.support.annotation.NonNull;",
                        "import android.support.annotation.Nullable;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent protected void onStart(@NonNull String event, @Nullable Object data) { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import android.support.annotation.Nullable;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnStart(@NonNull String event, @Nullable Object data) {",
                        "        dispatch(new OnStartEvent(event, data));",
                        "    }",
                        "    static final class OnStartEvent extends FeatureHost.Event {",
                        "        private final @NonNull String mEvent;",
                        "        private final @Nullable Object mData;",
                        "        OnStartEvent(@NonNull String event, @Nullable Object data) {",
                        "            mEvent = event;",
                        "            mData = data;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "                ((TestFeature) feature).onStart(mEvent, mData);",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void checkOnEventErrorInheritFeature() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "public class TestFeature {",
                        "    @FeatureEvent protected void onStart() { }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must inherit from");
    }

    @Test
    public void checkOnEventErrorExpectReturnVoid() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent protected boolean onStart() { return true; }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must return void");
    }

    @Test
    public void checkOnEventErrorExpectNotPrivate() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent private void onStart() { }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must not be private");
    }

    @Test
    public void checkOnEventErrorExpectNotStatic() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent static void onStart() { }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must not be static");
    }

    @Test
    public void checkFeatureInheritance() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatures",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "class FeatureA<FH extends FeatureAHost, C extends Context> extends Feature<FH, C> {",
                        "    @FeatureEvent protected void onMessageA() { }",
                        "}",
                        "",
                        "class FeatureB extends FeatureA<FeatureBHost, Context> {",
                        "    @FeatureEvent protected void onMessageB() { }",
                        "}"
                );

        JavaFileObject expectedFeatureHostA = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.FeatureAHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public abstract class FeatureAHost<FH extends FeatureAHost, C extends Context> extends FeatureHost<FH, C> {",
                        "    public FeatureHostA(@NonNull C context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public FH with(@NonNull FeatureA feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return (FH) this;",
                        "    }",
                        "    @NonNull public FH with(@NonNull FeatureA feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return (FH) this;",
                        "    }",
                        "    public void dispatchOnMessageA() {",
                        "        dispatch(new OnMessageAEvent());",
                        "    }",
                        "    static final class OnMessageAEvent extends FeatureHost.Event {",
                        "        @Override",
                        "        protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof FeatureA) {",
                        "                ((FeatureA) feature).onMessageA();",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        JavaFileObject expectedFeatureHostB = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.FeatureBHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class FeatureBHost extends FeatureAHost<FeatureBHost, Context> {",
                        "    public FeatureBHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public FeatureBHost with(@NonNull FeatureB feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public FeatureBHost with(@NonNull FeatureB feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnMessageB() {",
                        "        dispatch(new OnMessageBEvent());",
                        "    }",
                        "    static final class OnMessageBEvent extends FeatureHost.Event {",
                        "        @Override",
                        "        protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof FeatureB) {",
                        "                ((FeatureB) feature).onMessageB();",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedFeatureHostA, expectedFeatureHostB);

    }

    @Test
    public void checkFeatureInheritanceFeatureWithGenerics() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatures",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "class FeatureA<FH extends FeatureAHost, C extends Application> extends Feature<FH, C> {",
                        "    @FeatureEvent protected void onMessageA() { }",
                        "}"
                );

        JavaFileObject expectedFeatureHostA = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.FeatureAHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.app.Application;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public abstract class FeatureAHost<FH extends FeatureAHost, C extends Application> extends FeatureHost<FH, C> {",
                        "    public FeatureHostA(@NonNull C context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public FH with(@NonNull FeatureA feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return (FH) this;",
                        "    }",
                        "    @NonNull public FH with(@NonNull FeatureA feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return (FH) this;",
                        "    }",
                        "    public void dispatchOnMessageA() {",
                        "        dispatch(new OnMessageAEvent());",
                        "    }",
                        "    static final class OnMessageAEvent extends FeatureHost.Event {",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof FeatureA) {",
                        "                ((FeatureA) feature).onMessageA();",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedFeatureHostA);

    }

    @Test
    public void checkFeatureInheritanceFeatureInLibrary() throws Exception {

        // This is the case, when base feature has been defined in library
        // and used in the app. In this case processor won't be able analyse
        // base features' annotations.

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatures",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",

                        // this feature and its host are given (part of a library)

                        "class FeatureA<FH extends FeatureAHost, C extends Context> extends Feature<FH, C> {",
                        "    protected void onMessageA() { }",
                        "}",
                        "",
                        "class FeatureAHost<FH extends FeatureAHost, C extends Context> extends FeatureHost<FH, C> {",
                        "    public FeatureAHost(@NonNull C context) {",
                        "        super(context);",
                        "    }",
                        "}",

                        // this feature needs to be processed and a host needs to be generated

                        "class FeatureB extends FeatureA<FeatureBHost, Context> {",
                        "    @FeatureEvent protected void onMessageB() { }",
                        "}"
                );

        JavaFileObject expectedFeatureHostA = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.FeatureBHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class FeatureBHost extends FeatureAHost<FeatureBHost, Context> {",
                        "    public FeatureBHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public FeatureBHost with(@NonNull FeatureB feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public FeatureBHost with(@NonNull FeatureB feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnMessageB() {",
                        "        dispatch(new OnMessageBEvent());",
                        "    }",
                        "    static final class OnMessageBEvent extends FeatureHost.Event {",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof FeatureB) {",
                        "                ((FeatureB) feature).onMessageB();",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedFeatureHostA);

    }

    @Test
    public void checkOnEventWithArrayParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeature",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.FeatureEvent;",
                        "import de.halfbit.featured.Feature;",
                        "",
                        "public class TestFeature extends Feature<TestFeatureHost, Context> {",
                        "    @FeatureEvent protected void onCreate(@NonNull int[] value1, Object[] value2) { }",
                        "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceLines("de.halfbit.featured.test.TestFeatureHost",
                        "",
                        "package de.halfbit.featured.test;",
                        "",
                        "import android.content.Context;",
                        "import android.support.annotation.NonNull;",
                        "import de.halfbit.featured.Feature;",
                        "import de.halfbit.featured.FeatureHost;",
                        "",
                        "public class TestFeatureHost extends FeatureHost<TestFeatureHost, Context> {",
                        "    public TestFeatureHost(@NonNull Context context) {",
                        "        super(context);",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature) {",
                        "        addFeature(feature, feature.getClass().toString());",
                        "        return this;",
                        "    }",
                        "    @NonNull public TestFeatureHost with(@NonNull TestFeature feature, @NonNull String featureName) {",
                        "        addFeature(feature, featureName);",
                        "        return this;",
                        "    }",
                        "    public void dispatchOnCreate(@NonNull int[] value1, Object[] value2) {",
                        "        dispatch(new OnCreateEvent(value1, value2));",
                        "    }",
                        "    static final class OnCreateEvent extends FeatureHost.Event {",
                        "        private final @NonNull int[] mValue1;",
                        "        private final Object[] mValue2;",
                        "        OnCreateEvent(@NonNull int[] value1, Object[] value2) {",
                        "            mValue1 = value1;",
                        "            mValue2 = value2;",
                        "        }",
                        "        @Override protected void dispatch(@NonNull Feature feature) {",
                        "            if (feature instanceof TestFeature) {",
                        "                ((TestFeature) feature).onCreate(mValue1, mValue2);",
                        "            }",
                        "        }",
                        "    }",
                        "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);

    }

}
