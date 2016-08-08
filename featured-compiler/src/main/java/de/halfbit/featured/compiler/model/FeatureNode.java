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

import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

public class FeatureNode {

    private final TypeElement mElement;
    private final Map<Name, MethodNode> mMethods = new LinkedHashMap<>();
    private final String mName;
    private final boolean mIsLibraryNode;
    private FeatureNode mSuperFeatureNode;
    private boolean mHasExtendingFeatureNodes;
    private boolean mIsValid;

    public FeatureNode(String featureName, TypeElement element) {
        mName = featureName;
        mElement = element;
        mIsLibraryNode = false;
    }

    public FeatureNode(String featureName, TypeElement element, boolean isLibraryNode) {
        mName = featureName;
        mElement = element;
        mIsLibraryNode = isLibraryNode;
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
        boolean doNext = visitor.onFeatureEnter(this);
        if (!doNext) {
            return;
        }
        Collection<MethodNode> methodElements = mMethods.values();
        for (MethodNode methodElement : methodElements) {
            methodElement.accept(visitor);
        }
        visitor.onFeatureExit(this);
    }

    public void setSuperFeatureNode(FeatureNode superFeatureNode) {
        mSuperFeatureNode = superFeatureNode;
        mSuperFeatureNode.setHasInheritingFeatureNodes(true);
    }

    @Nullable public FeatureNode getSuperFeatureNode() {
        return mSuperFeatureNode;
    }

    public void setHasInheritingFeatureNodes(boolean hasExtendingFeatureNodes) {
        mHasExtendingFeatureNodes = hasExtendingFeatureNodes;
    }

    public boolean hasInheritingFeatureNodes() {
        return mHasExtendingFeatureNodes;
    }

    public boolean isLibraryNode() {
        return mIsLibraryNode;
    }

    public String getName() {
        return mName;
    }
}
