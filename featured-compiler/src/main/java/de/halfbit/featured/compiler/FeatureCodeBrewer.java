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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import de.halfbit.featured.compiler.model.FeatureNode;
import de.halfbit.featured.compiler.model.MethodNode;
import de.halfbit.featured.compiler.model.ModelNodeVisitor;
import de.halfbit.featured.compiler.model.ParameterNode;

public class FeatureCodeBrewer implements ModelNodeVisitor {

    private final Names mNames;

    private ClassName mFeatureHostClassName;
    private TypeSpec.Builder mFeatureHostTypeBuilder;
    private ClassName mEventClassName;
    private TypeSpec.Builder mEventTypeBuilder;
    private MethodSpec.Builder mEventConstructorBuilder;
    private MethodSpec.Builder mDispachMethodBuilder;
    private StringBuilder mListedFields;
    private StringBuilder mListedParams;

    public FeatureCodeBrewer(Names names) {
        mNames = names;
    }

    @Override public boolean onFeatureEnter(FeatureNode featureNode) {
        if (featureNode.isLibraryNode()) {
            return false;
        }

        mFeatureHostClassName = mNames.getFeatureHostClassName(featureNode);

        // public class FeatureBHost extends FeatureAHost<FeatureB, FeatureBHost> { ...

        // public class FeatureAHost<F extends FeatureA, FH extends FeatureAHost>
        //                      extends FeatureHost<F, FH> { ...


        TypeName superFeatureHostType = mNames.getFeatureHostSuperTypeName(featureNode);

        if (featureNode.hasInheritingFeatureNodes()) {

            mFeatureHostTypeBuilder = TypeSpec
                    .classBuilder(mFeatureHostClassName.simpleName())
                    .addTypeVariable(TypeVariableName.get("F", mNames.getFeatureClassName(featureNode)))
                    .addTypeVariable(TypeVariableName.get("FH", mFeatureHostClassName))
                    .superclass(superFeatureHostType)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec
                                    .builder(mNames.getContextClassName(), "context")
                                    .addAnnotation(mNames.getNonNullClassName())
                                    .build())
                            .addStatement("super(context)")
                            .build());

        } else {

            mFeatureHostTypeBuilder = TypeSpec
                    .classBuilder(mFeatureHostClassName.simpleName())
                    .superclass(superFeatureHostType)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec
                                    .builder(mNames.getContextClassName(), "context")
                                    .addAnnotation(mNames.getNonNullClassName())
                                    .build())
                            .addStatement("super(context)")
                            .build());
        }

        return true;
    }

    @Override public void onMethodEnter(MethodNode methodElement) {
        mEventClassName = mNames.getEventClassName(methodElement);

        mEventTypeBuilder = TypeSpec.classBuilder(mEventClassName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .superclass(mNames.getEventSuperClassName(methodElement));

        if (methodElement.hasParameters()) {
            mEventConstructorBuilder = MethodSpec.constructorBuilder();
            mListedFields = prepareStringBuilder(mListedFields);
            mListedParams = prepareStringBuilder(mListedParams);
        }

        mDispachMethodBuilder = MethodSpec
                .methodBuilder(mNames.getDispatchMethodName(methodElement))
                .addModifiers(Modifier.PUBLIC);

    }

    @Override public void onParameter(ParameterNode param) {

        String fieldName = "m" + Names.capitalize(param.getName());
        if (!param.isDispatchCompleted()) {
            mEventTypeBuilder.addField(
                    param.getType(), fieldName, Modifier.PRIVATE, Modifier.FINAL);
            mListedFields.append(fieldName).append(", ");
        }
        mListedParams.append(param.getName()).append(", ");

        mEventConstructorBuilder
                .addParameter(param.getType(), param.getName())
                .addStatement("$L = $L", fieldName, param.getName());

        mDispachMethodBuilder
                .addParameter(param.getType(), param.getName());
    }

    @Override public void onMethodExit(MethodNode methodElement) {

        String fieldNames = "";
        String paramNames = "";
        if (methodElement.hasParameters()) {
            mEventTypeBuilder.addMethod(mEventConstructorBuilder.build());
            fieldNames = removeLastComma(mListedFields).toString();
            paramNames = removeLastComma(mListedParams).toString();
        }

        mFeatureHostTypeBuilder
                // event class
                .addType(mEventTypeBuilder
                        .addMethod(MethodSpec.methodBuilder("dispatch")
                                .addModifiers(Modifier.PROTECTED)
                                .addAnnotation(mNames.getOverrideClassName())
                                .addParameter(mNames.getFeatureClassName(
                                        methodElement.getParent()), "feature")
                                .addStatement("feature.$L($L)",
                                        mNames.getFeatureMethodName(methodElement), fieldNames)
                                .build())
                        .build())
                // dispatch event method
                .addMethod(mDispachMethodBuilder
                        .addStatement("dispatch(new $T($L))", mEventClassName, paramNames)
                        .build());


    }

    @Override public void onFeatureExit(FeatureNode featureNode) {
        // nop
    }

    private static StringBuilder prepareStringBuilder(StringBuilder builder) {
        if (builder == null) {
            return new StringBuilder(40);
        }
        builder.setLength(0);
        return builder;
    }

    private static StringBuilder removeLastComma(StringBuilder builder) {
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }
        return builder;
    }

    public void brewTo(Filer filer) throws IOException {
        JavaFile javaFile = JavaFile
                .builder(mFeatureHostClassName.packageName(), mFeatureHostTypeBuilder.build())
                .addFileComment("Featured code. Do not modify!")
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
        javaFile.writeTo(filer);
    }

}
