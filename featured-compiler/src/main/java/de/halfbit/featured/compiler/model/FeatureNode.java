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
package de.halfbit.featured.compiler.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

public class FeatureNode {

    private final TypeElement mElement;
    private final Map<Name, MethodNode> mMethods = new LinkedHashMap<>();

    private boolean mIsValid;

    public FeatureNode(TypeElement element) {
        mElement = element;
    }

    public boolean hasMethod(Name name) {
        return mMethods.containsKey(name);
    }

    public void addMethod(Name name, MethodNode method) {
        mMethods.put(name, method);
    }

    public TypeElement getElement() {
        return mElement;
    }

    public boolean isValid() {
        return mIsValid;
    }

    public void setValid(boolean valid) {
        mIsValid = valid;
    }

    public void accept(ModelNodeVisitor visitor) {
        visitor.onFeatureEnter(this);
        Collection<MethodNode> methodElements = mMethods.values();
        for (MethodNode methodElement : methodElements) {
            methodElement.accept(visitor);
        }
        visitor.onFeatureExit(this);
    }

}
