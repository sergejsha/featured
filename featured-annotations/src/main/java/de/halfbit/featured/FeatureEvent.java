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
package de.halfbit.featured;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Method annotation to be used in a base feature as a feature callbacks marker. For each
 * class with annotated methods the library will generate a custom feature host class
 * with dispatch methods corresponding to annotated feature callbacks. See {@code Feature}
 * and {@code FeatureHost} for more detailed information.
 *
 * @author sergej shafarenka
 */
@Retention(CLASS) @Target(METHOD)
public @interface FeatureEvent {

    /**
     * If set to true, generated class will have a {@code FeatureHost.OnDispatchCompleted}
     * a the last parameter of corresponding generated dispatch-method.
     *
     * @return the flag defining whether {@code FeatureHost.OnDispatchCompleted} shall be
     * added the last feature callback parameter to corresponding generated dispatch-method.
     */
    boolean dispatchCompleted() default false;

}
