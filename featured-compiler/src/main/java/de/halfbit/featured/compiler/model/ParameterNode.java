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

import com.squareup.javapoet.TypeName;

public class ParameterNode {

    private final MethodNode mMethodNode;
    private String mName;
    private TypeName mType;
    private boolean mIsDispatchCompleted;

    public ParameterNode(MethodNode methodNode) {
        mMethodNode = methodNode;
    }

    public String getName() {
        return mName;
    }

    public ParameterNode setName(String name) {
        mName = name;
        return this;
    }

    public TypeName getType() {
        return mType;
    }

    public ParameterNode setType(TypeName type) {
        mType = type;
        return this;
    }

    public boolean isDispatchCompleted() {
        return mIsDispatchCompleted;
    }

    public ParameterNode setDispatchCompleted(boolean dispatchCompleted) {
        mIsDispatchCompleted = dispatchCompleted;
        return this;
    }

    public void accept(ModelNodeVisitor visitor) {
        visitor.onParameter(this);
    }

    public MethodNode getParent() {
        return mMethodNode;
    }
}
