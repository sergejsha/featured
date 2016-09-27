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
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import de.halfbit.featured.compiler.model.FeatureNode;
import de.halfbit.featured.compiler.model.MethodNode;

public class Names {

    public static final String PACKAGE_NAME = "de.halfbit.featured";

    private static final ClassName FEATURE =
            ClassName.get(PACKAGE_NAME, "Feature");
    private static final ClassName FEATURE_EVENT =
            ClassName.get(PACKAGE_NAME, "FeatureEvent");
    private static final ClassName FEATURE_HOST =
            ClassName.get(PACKAGE_NAME, "FeatureHost");
    private static final ClassName FEATURE_HOST_EVENT =
            ClassName.get(PACKAGE_NAME, "FeatureHost", "Event");
    private static final ClassName FEATURE_HOST_DISPATCH_COMPLETED =
            ClassName.get(PACKAGE_NAME, "FeatureHost", "OnDispatchCompleted");
    private static final ClassName CONTEXT =
            ClassName.get("android.content", "Context");
    private static final ClassName NOT_NULL =
            ClassName.get("android.support.annotation", "NonNull");
    private static final ClassName OVERRIDE =
            ClassName.get("java.lang", "Override");

    private static final int HOST_PARAMETER_INDEX = 0;
    private static final int CONTEXT_PARAMETER_INDEX = 1;

    private final Elements mElementUtils;
    private final Types mTypeUtils;

    public Names(Elements elementUtils, Types typeUtils) {
        mElementUtils = elementUtils;
        mTypeUtils = typeUtils;
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    public ClassName getFeatureClassName(FeatureNode featureNode) {
        TypeElement element = featureNode.getElement();
        return ClassName.get(getPackageName(element), element.getSimpleName().toString());
    }

    public ClassName getFeatureHostClassName(FeatureNode featureNode) {

        // we read host name from the feature class parameters

        TypeMirror superType = featureNode.getElement().getSuperclass();
        if (superType.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException();
        }

        TypeName featureHostName = getFeatureParameterTypeVariableName((DeclaredType) superType, HOST_PARAMETER_INDEX);
        ClassName featureHostClassName = null;

        if (featureHostName instanceof ClassName) {
            // FeatureA extends Feature<FeatureAHost, Context>
            featureHostClassName = (ClassName) featureHostName;
        }

        if (featureHostName instanceof TypeVariableName) {
            // FeatureA<FH extends FeatureAHost, C extends Context> extends Feature<FH, C>
            TypeVariableName featureHostVariableTypeName = (TypeVariableName) featureHostName;
            if (featureHostVariableTypeName.bounds.size() <= HOST_PARAMETER_INDEX) {
                throw new IllegalArgumentException("Missing feature host parameter. \n"
                        + featureNode + "\n" + featureHostVariableTypeName);
            }
            TypeName featureHostTypeName = featureHostVariableTypeName.bounds.get(HOST_PARAMETER_INDEX);
            if (featureHostTypeName instanceof ClassName) {
                featureHostClassName = (ClassName) featureHostTypeName;
            }
        }

        if (featureHostClassName == null) {
            throw new IllegalArgumentException("Unsupported feature host name declaration. \n"
                    + featureNode + "\n" + featureHostName);
        }

        PackageElement packageElement = mElementUtils.getPackageOf(featureNode.getElement());
        return ClassName.get(packageElement.toString(), featureHostClassName.simpleName());
    }

    public ClassName getFeatureContextSuperClassName(FeatureNode featureNode) {
        return getFeatureParameterClass(featureNode, CONTEXT_PARAMETER_INDEX);
    }

    private ClassName getFeatureParameterClass(FeatureNode featureNode, int parameterIndex) {
        TypeMirror superClass = featureNode.getElement().getSuperclass();
        if (superClass.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException(
                    "Check model validator. It must check feature super class.");
        }

        Element argElem = getFeatureParameterElement((DeclaredType) superClass, parameterIndex);
        if (argElem == null || argElem.getKind() == ElementKind.TYPE_PARAMETER) {
            return null;
        }
        return ClassName.get((TypeElement) argElem);
    }

    public Element getFeatureParameterElement(DeclaredType clazz, int parameterIndex) {
        List<? extends TypeMirror> args = clazz.getTypeArguments();
        if (parameterIndex >= args.size()) {
            return null;
        }
        return mTypeUtils.asElement(args.get(parameterIndex));
    }

    public TypeVariableName getFeatureContextTypeVariableName(FeatureNode featureNode) {
        return getParameterTypeVariableName(featureNode, CONTEXT_PARAMETER_INDEX);
    }

    public TypeVariableName getFeatureHostTypeVariableName(FeatureNode featureNode) {
        return getParameterTypeVariableName(featureNode, HOST_PARAMETER_INDEX);
    }

    private TypeVariableName getParameterTypeVariableName(FeatureNode featureNode, int parameterIndex) {
        TypeMirror type = featureNode.getElement().asType();
        if (type.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException("FeatureNode type is not supported: " + featureNode);
        }
        Element paramElem = getFeatureParameterElement((DeclaredType) type, parameterIndex);
        if (paramElem.getKind() == ElementKind.TYPE_PARAMETER) {
            return TypeVariableName.get((TypeVariable) paramElem.asType());
        }
        throw new IllegalArgumentException(
                "Expecting paramElem of kind TYPE_PARAMETER, while received" + paramElem);
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

    public TypeName getEventSuperClassName() {
        return FEATURE_HOST_EVENT;
    }

    public String getFeatureMethodName(MethodNode methodElement) {
        return methodElement.getElement().getSimpleName().toString();
    }

    public TypeName getSuggestedSuperFeatureTypeName(FeatureNode featureNode) {
        String featureHostPackage = getPackageName(featureNode.getElement());
        String featureHostName = featureNode.getName() + "Host";
        ClassName suggestedFeatureHostClassName = ClassName.get(featureHostPackage, featureHostName);
        return ParameterizedTypeName.get(FEATURE, suggestedFeatureHostClassName, CONTEXT);
    }

    public TypeName getFeatureHostSuperTypeName(FeatureNode featureNode) {

        // public class FeatureA extends Feature<FeatureAHost, Context> {
        // public class FeatureA<FH extends FeatureAHost, C extends Context> extends Feature<FH, C> {
        // public class FeatureB extends FeatureA<FeatureBHost, Context> {

        TypeMirror superType = featureNode.getElement().getSuperclass();
        if (superType.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException();
        }

        TypeName hostTypeName = getFeatureParameterTypeVariableName(
                (DeclaredType) superType, HOST_PARAMETER_INDEX);

        TypeName contextTypeName = getFeatureParameterTypeVariableName(
                (DeclaredType) superType, CONTEXT_PARAMETER_INDEX);

        FeatureNode superFeatureNode = featureNode.getSuperFeatureNode();
        if (superFeatureNode == null) {
            DeclaredType declaredType = (DeclaredType) superType;
            TypeName featureClassName = ClassName.get(declaredType.asElement().asType());
            if (featureClassName instanceof ParameterizedTypeName) {
                ParameterizedTypeName ptn = (ParameterizedTypeName) featureClassName;
                ClassName featureHostClass = ClassName.get(
                        ptn.rawType.packageName(), ptn.rawType.simpleName() + "Host");
                return ParameterizedTypeName.get(featureHostClass, hostTypeName, contextTypeName);
            }
        }

        ClassName featureHostClass = superFeatureNode == null
                ? FEATURE_HOST : getFeatureHostClassName(superFeatureNode);

        return ParameterizedTypeName.get(featureHostClass, hostTypeName, contextTypeName);
    }

    public TypeName getFeatureHostParameterTypeName(FeatureNode featureNode) {
        TypeMirror type = featureNode.getElement().asType();
        if (type.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException();
        }
        return getFeatureParameterTypeVariableName((DeclaredType) type, HOST_PARAMETER_INDEX);
    }

    private TypeName getFeatureParameterTypeVariableName(DeclaredType featureType,
                                                         int featureParameterIndex) {
        Element paramElem = getFeatureParameterElement(featureType, featureParameterIndex);
        if (paramElem == null) {
            return null;
        }

        if (paramElem.getKind() == ElementKind.TYPE_PARAMETER) {
            return TypeVariableName.get((TypeVariable) paramElem.asType());

        } else if (paramElem.getKind() == ElementKind.CLASS) {
            return TypeName.get(paramElem.asType());
        }

        return null;
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
                    List<AnnotationSpec> annotationSpecs =
                            new ArrayList<>(annotationMirrors.size());
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
