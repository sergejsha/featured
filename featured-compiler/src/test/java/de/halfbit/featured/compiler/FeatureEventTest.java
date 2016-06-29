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

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class FeatureEventTest {

    @Test public void checkOnEvent() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent protected void onStart() { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart() {\n"
                        + "        dispatch(new OnStartEvent());\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart();\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventWithParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent protected void onStart(long time, boolean valid, int count, Object state) { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart(long time, boolean valid, int count, Object state) {\n"
                        + "        dispatch(new OnStartEvent(time, valid, count, state));\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        private final long mTime;\n"
                        + "        private final boolean mValid;\n"
                        + "        private final int mCount;\n"
                        + "        private final Object mState;\n"
                        + "        OnStartEvent(long time, boolean valid, int count, Object state) {\n"
                        + "            mTime = time;\n"
                        + "            mValid = valid;\n"
                        + "            mCount = count;\n"
                        + "            mState = state;\n"
                        + "        }\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart(mTime, mValid, mCount, mState);\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventWithParametersGenerics() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "import java.util.List;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent protected void onStart(List<String> names) { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "import java.util.List;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart(List<String> names) {\n"
                        + "        dispatch(new OnStartEvent(names));\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        private final List<String> mNames;\n"
                        + "        OnStartEvent(List<String> names) {\n"
                        + "            mNames = names;\n"
                        + "        }\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart(mNames);\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventDispatchCompleted() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent(dispatchCompleted = true) protected void onStart() { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart(FeatureHost.OnDispatchCompleted onDispatchCompleted) {\n"
                        + "        dispatch(new OnStartEvent(onDispatchCompleted));\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        OnStartEvent(FeatureHost.OnDispatchCompleted onDispatchCompleted) {\n"
                        + "            mOnDispatchCompleted = onDispatchCompleted;\n"
                        + "        }\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart();\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventDispatchCompletedWithParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent(dispatchCompleted = true) protected void onStart(int time) { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart(int time, FeatureHost.OnDispatchCompleted onDispatchCompleted) {\n"
                        + "        dispatch(new OnStartEvent(time, onDispatchCompleted));\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        private final int mTime;\n"
                        + "        OnStartEvent(int time, FeatureHost.OnDispatchCompleted onDispatchCompleted) {\n"
                        + "            mTime = time;\n"
                        + "            mOnDispatchCompleted = onDispatchCompleted;\n"
                        + "        }\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart(mTime);\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventWithAnnotatedParameters() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import android.support.annotation.Nullable;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent protected void onStart(@NonNull String event, @Nullable Object data) { }\n"
                        + "}"
                );

        JavaFileObject expectedSource = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeatureHost", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import android.content.Context;\n"
                        + "import android.support.annotation.NonNull;\n"
                        + "import android.support.annotation.Nullable;\n"
                        + "import de.halfbit.featured.FeatureHost;\n"
                        + "public class TestFeatureHost extends FeatureHost<TestFeature, TestFeatureHost> {\n"
                        + "    public TestFeatureHost(@NonNull Context context) {\n"
                        + "        super(context);\n"
                        + "    }\n"
                        + "    public void dispatchOnStart(@NonNull String event, @Nullable Object data) {\n"
                        + "        dispatch(new OnStartEvent(event, data));\n"
                        + "    }\n"
                        + "    private static final class OnStartEvent extends FeatureHost.Event<TestFeature> {\n"
                        + "        private final @NonNull String mEvent;\n"
                        + "        private final @Nullable Object mData;\n"
                        + "        OnStartEvent(@NonNull String event, @Nullable Object data) {\n"
                        + "            mEvent = event;\n"
                        + "            mData = data;\n"
                        + "        }\n"
                        + "        @Override protected void dispatch(TestFeature feature) {\n"
                        + "            feature.onStart(mEvent, mData);\n"
                        + "        }\n"
                        + "    }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void checkOnEventErrorInheritFeature() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature {\n"
                        + "    @FeatureEvent protected void onStart() { }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must inherit from");
    }

    @Test public void checkOnEventErrorExpectReturnVoid() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent protected boolean onStart() { return true; }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must return void");
    }

    @Test public void checkOnEventErrorExpectNotPrivate() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent private void onStart() { }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must not be private or static");
    }

    @Test public void checkOnEventErrorExpectNotStatic() throws Exception {

        JavaFileObject source = JavaFileObjects
                .forSourceString("de.halfbit.featured.test.TestFeature", ""
                        + "package de.halfbit.featured.test;\n"
                        + "import de.halfbit.featured.FeatureEvent;\n"
                        + "import de.halfbit.featured.Feature;\n"
                        + "public class TestFeature extends Feature<TestFeatureHost> {\n"
                        + "    @FeatureEvent static void onStart() { }\n"
                        + "}"
                );

        assertAbout(javaSource()).that(source)
                .processedWith(new FeatureProcessor())
                .failsToCompile()
                .withErrorContaining("must not be private or static");
    }

}
