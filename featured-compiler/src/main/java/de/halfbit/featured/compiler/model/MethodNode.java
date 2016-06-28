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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

import de.halfbit.featured.FeatureEvent;

public class MethodNode {

    private final ExecutableElement mElement;
    private final FeatureNode mParent;
    private List<ParameterNode> mParameterNodes;

    public MethodNode(ExecutableElement element, FeatureNode parent) {
        mElement = element;
        mParent = parent;
    }

    public void accept(ModelNodeVisitor visitor) {
        visitor.onMethodEnter(this);
        if (mParameterNodes != null) {
            for (ParameterNode parameterNode : mParameterNodes) {
                parameterNode.accept(visitor);
            }
        }
        visitor.onMethodExit(this);
    }

    public ExecutableElement getElement() {
        return mElement;
    }

    public FeatureNode getParent() {
        return mParent;
    }

    public boolean hasDispatchCompletedParameter() {
        FeatureEvent ann = mElement.getAnnotation(FeatureEvent.class);
        return ann != null && ann.dispatchCompleted();
    }

    public void addParameter(ParameterNode parameter) {
        if (mParameterNodes == null) {
            mParameterNodes = new ArrayList<>(6);
        }
        mParameterNodes.add(parameter);
    }

    public boolean hasParameters() {
        return mParameterNodes != null;
    }
}
