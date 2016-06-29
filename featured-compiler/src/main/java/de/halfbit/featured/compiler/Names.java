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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import de.halfbit.featured.compiler.model.FeatureNode;
import de.halfbit.featured.compiler.model.MethodNode;

public class Names {

    private static final ClassName FEATURE =
            ClassName.get("de.halfbit.featured", "Feature");
    private static final ClassName FEATURE_EVENT =
            ClassName.get("de.halfbit.featured", "FeatureEvent");
    private static final ClassName FEATURE_HOST =
            ClassName.get("de.halfbit.featured", "FeatureHost");
    private static final ClassName FEATURE_HOST_EVENT =
            ClassName.get("de.halfbit.featured", "FeatureHost", "Event");
    private static final ClassName FEATURE_HOST_DISPATCH_COMPLETED =
            ClassName.get("de.halfbit.featured", "FeatureHost", "OnDispatchCompleted");
    private static final ClassName CONTEXT =
            ClassName.get("android.content", "Context");
    private static final ClassName NOT_NULL =
            ClassName.get("android.support.annotation", "NonNull");
    private static final ClassName OVERRIDE =
            ClassName.get("java.lang", "Override");

    private final Elements mElementUtils;

    public Names(Elements elementUtils) {
        mElementUtils = elementUtils;
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    public ClassName getFeatureClassName(FeatureNode featureNode) {
        TypeElement element = featureNode.getElement();
        return ClassName.get(getPackageName(element), element.getSimpleName().toString());
    }

    public ClassName getFeatureHostClassName(FeatureNode featureNode) {
        TypeElement element = featureNode.getElement();
        return ClassName.get(getPackageName(element), element.getSimpleName().toString() + "Host");
    }

    public TypeName getFeatureHostSuperTypeName(FeatureNode featureNode) {
        ClassName featureType = getFeatureClassName(featureNode);
        ClassName featureHostType = getFeatureHostClassName(featureNode);
        return ParameterizedTypeName.get(FEATURE_HOST, featureType, featureHostType);
    }

    public ClassName getContextClassName() {
        return CONTEXT;
    }

    public ClassName getNonNullClassName() {
        return NOT_NULL;
    }

    public ClassName getOverrideClassName() {
        return OVERRIDE;
    }

    public ClassName getFeatureClassName() {
        return FEATURE;
    }

    public ClassName getFeatureEventClassName() {
        return FEATURE_EVENT;
    }

    public String getDispatchMethodName(MethodNode methodElement) {
        String methodName = methodElement.getElement().getSimpleName().toString();
        return "dispatch" + capitalize(methodName);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length());
    }

    public ClassName getEventClassName(MethodNode methodElement) {
        ClassName parentName = getFeatureHostClassName(methodElement.getParent());
        String eventName = getEventName(methodElement);
        return ClassName.get(parentName.packageName(), parentName.simpleName(), eventName);
    }

    private String getEventName(MethodNode methodElement) {
        return capitalize(getFeatureMethodName(methodElement)) + "Event";
    }

    public TypeName getEventSuperClassName(MethodNode methodElement) {
        ClassName featureType = getFeatureClassName(methodElement.getParent());
        return ParameterizedTypeName.get(FEATURE_HOST_EVENT, featureType);
    }

    public String getFeatureMethodName(MethodNode methodElement) {
        return methodElement.getElement().getSimpleName().toString();
    }

    public TypeName getFeatureSuperTypeName(FeatureNode featureNode) {
        ClassName featureHostClass = getFeatureHostClassName(featureNode);
        return ParameterizedTypeName.get(FEATURE, featureHostClass);
    }

    public TypeName getTypeNameByKind(VariableElement param) {
        switch (param.asType().getKind()) {

            case BOOLEAN:
                return TypeName.BOOLEAN;
            case BYTE:
                return TypeName.BYTE;
            case CHAR:
                return TypeName.CHAR;
            case DOUBLE:
                return TypeName.DOUBLE;
            case FLOAT:
                return TypeName.FLOAT;
            case INT:
                return TypeName.INT;
            case LONG:
                return TypeName.LONG;
            case SHORT:
                return TypeName.SHORT;

            case DECLARED:
                TypeMirror type = param.asType();
                TypeName typeName = ClassName.get(type);

                List<? extends AnnotationMirror> annotationMirrors = param.getAnnotationMirrors();
                if (annotationMirrors.size() > 0) {
                    List<AnnotationSpec> annotationSpecs = new ArrayList<>(annotationMirrors.size());
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        annotationSpecs.add(AnnotationSpec.get(annotationMirror));
                    }
                    typeName = typeName.annotated(annotationSpecs);
                }

                return typeName;

            default:
                throw new IllegalStateException("unsupported kind: " + param.asType().getKind());
        }
    }

    public ClassName getDispatchCompletedClassName() {
        return FEATURE_HOST_DISPATCH_COMPLETED;
    }

}
