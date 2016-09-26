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

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import de.halfbit.featured.compiler.model.FeatureNode;
import de.halfbit.featured.compiler.model.MethodNode;
import de.halfbit.featured.compiler.model.ModelNodeVisitor;
import de.halfbit.featured.compiler.model.ParameterNode;

public class FeatureModelValidator implements ModelNodeVisitor {

    private final Messager mMessager;
    private final Names mNames;

    public FeatureModelValidator(Messager messager, Names names) {
        mMessager = messager;
        mNames = names;
    }

    @Override public boolean onFeatureEnter(FeatureNode featureNode) {
        featureNode.setValid(true);

        // verify extends Feature<?,?> class
        TypeElement element = featureNode.getElement();
        String superType = mNames.getFeatureClassName().toString() + "<?,?>";
        if (!isSubtypeOfType(element.asType(), superType)) {
            error(featureNode, element,
                    "%s must inherit from %s.", element.getQualifiedName(),
                    mNames.getFeatureSuperTypeName(featureNode).toString());
        }

        return true;
    }

    @Override public void onMethodEnter(MethodNode methodElement) {

        ExecutableElement element = methodElement.getElement();

        // verify return void
        TypeMirror returnType = element.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            error(methodElement.getParent(), element,
                    "Feature event method %s() must return void.",
                    element.getSimpleName());
        }

        Set<Modifier> modifiers = element.getModifiers();

        // verify not private
        if (modifiers.contains(Modifier.PRIVATE)) {
            error(methodElement.getParent(), element,
                    "@%s void %s() must not be private.",
                    mNames.getFeatureEventClassName(), element.getSimpleName());
        }

        // verify not static
        if (modifiers.contains(Modifier.STATIC)) {
            error(methodElement.getParent(), element,
                    "@%s void %s() must not be static.",
                    mNames.getFeatureEventClassName(), element.getSimpleName());
        }
    }

    @Override public void onParameter(ParameterNode parameter) {
        // nop
    }

    @Override public void onMethodExit(MethodNode methodElement) {
        // nop
    }

    @Override public void onFeatureExit(FeatureNode featureNode) {
        // nop
    }

    // Copyright 2013 Jake Wharton, http://jakewharton.github.com/butterknife/
    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private void error(FeatureNode featureNode, Element element, String msg, Object... args) {
        featureNode.setValid(false);
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }

    public void error(Element element, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }

}
