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
import com.squareup.javapoet.CodeBlock;
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

import static de.halfbit.featured.compiler.Assertions.assertNotNull;

class FeatureCodeBrewer implements ModelNodeVisitor {

    private final Names mNames;

    // feature host
    private ClassName mFeatureClassName;
    private ClassName mFeatureHostClassName;
    private TypeSpec.Builder mFeatureHostTypeBuilder;

    // event
    private ClassName mEventClassName;
    private TypeSpec.Builder mEventTypeBuilder;
    private MethodSpec.Builder mEventConstructorBuilder;
    private MethodSpec.Builder mEventDispatchMethodBuilder;

    // common
    private StringBuilder mListedFields;
    private StringBuilder mListedParams;

    FeatureCodeBrewer(Names names) {
        mNames = names;
    }

    @Override
    public boolean onFeatureEnter(FeatureNode featureNode) {
        if (featureNode.isLibraryNode()) {
            return false;
        }

        mFeatureClassName = mNames.getFeatureClassName(featureNode);
        mFeatureHostClassName = mNames.getFeatureHostClassName(featureNode);

        brewClassFeatureHost(featureNode);
        brewMethodWith(featureNode);

        return true;
    }

    @Override
    public void onMethodEnter(MethodNode methodElement) {
        mEventClassName = mNames.getEventClassName(methodElement);

        mEventTypeBuilder = TypeSpec.classBuilder(mEventClassName)
                .addModifiers(Modifier.STATIC, Modifier.FINAL)
                .superclass(mNames.getEventSuperClassName());

        if (methodElement.hasParameters()) {
            mEventConstructorBuilder = MethodSpec.constructorBuilder();
            mListedFields = prepareStringBuilder(mListedFields);
            mListedParams = prepareStringBuilder(mListedParams);
        }

        mEventDispatchMethodBuilder = MethodSpec
                .methodBuilder(mNames.getDispatchMethodName(methodElement))
                .addModifiers(Modifier.PUBLIC);
    }

    @Override
    public void onParameter(ParameterNode param) {

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

        mEventDispatchMethodBuilder
                .addParameter(param.getType(), param.getName());
    }

    @Override
    public void onMethodExit(MethodNode methodElement) {

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
                                .addParameter(ParameterSpec.builder(mNames.getFeatureClassName(), "feature")
                                        .addAnnotation(mNames.getNonNullClassName())
                                        .build())
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("if (feature instanceof $T)", mFeatureClassName)
                                        .addStatement("(($T) feature).$L($L)", mFeatureClassName,
                                                mNames.getFeatureMethodName(methodElement), fieldNames)
                                        .endControlFlow()
                                        .build())
                                .build())
                        .build())

                // dispatch method
                .addMethod(mEventDispatchMethodBuilder
                        .addStatement("dispatch(new $T($L))", mEventClassName, paramNames)
                        .build());
    }

    @Override
    public void onFeatureExit(FeatureNode featureNode) {
        // nop
    }

    private void brewClassFeatureHost(FeatureNode featureNode) {
        TypeName superFeatureHostType = mNames.getFeatureHostSuperTypeName(featureNode);
        ClassName featureContextSuperClassName = mNames.getFeatureContextSuperClassName(featureNode);

        if (featureNode.hasInheritingFeatureNodes()) {
            // public abstract class FeatureAHost<FH extends FeatureAHost, C extends Context>
            //                      extends FeatureHost<FH, C> {

            TypeVariableName contextTypeVariable = mNames.getFeatureContextTypeVariableName(featureNode);
            TypeVariableName featureHostType = mNames.getFeatureHostTypeVariableName(featureNode);

            mFeatureHostTypeBuilder = TypeSpec
                    .classBuilder(mFeatureHostClassName.simpleName())
                    .addTypeVariable(assertNotNull(featureHostType, featureNode))
                    .addTypeVariable(assertNotNull(contextTypeVariable, featureNode))
                    .superclass(superFeatureHostType)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec
                                    .builder(contextTypeVariable, "context")
                                    .addAnnotation(mNames.getNonNullClassName())
                                    .build())
                            .addStatement("super(context)")
                            .build());

        } else {
            // public class FeatureBHost extends FeatureAHost<FeatureBHost, Context> {

            mFeatureHostTypeBuilder = TypeSpec
                    .classBuilder(mFeatureHostClassName.simpleName())
                    .superclass(superFeatureHostType)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec
                                    .builder(featureContextSuperClassName, "context")
                                    .addAnnotation(mNames.getNonNullClassName())
                                    .build())
                            .addStatement("super(context)")
                            .build());
        }
    }

    private void brewMethodWith(FeatureNode featureNode) {
        MethodSpec.Builder withMethod = MethodSpec.methodBuilder("with")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(mNames.getNonNullClassName())
                .addParameter(ParameterSpec.builder(mFeatureClassName, "feature")
                        .addAnnotation(mNames.getNonNullClassName())
                        .build());

        TypeName featureHostType = mNames.getFeatureHostParameterTypeName(featureNode);

        if (featureHostType == null) {
            withMethod.addCode(CodeBlock.builder()
                    .addStatement("addFeature(feature)")
                    .addStatement("return this")
                    .build())
                    .returns(mFeatureHostClassName);

        } else {
            withMethod.addCode(CodeBlock.builder()
                    .addStatement("addFeature(feature)")
                    .addStatement("return ($T) this", featureHostType)
                    .build())
                    .returns(featureHostType);
        }

        mFeatureHostTypeBuilder
                .addMethod(withMethod.build());
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

    void brewTo(Filer filer) throws IOException {
        JavaFile javaFile = JavaFile
                .builder(mFeatureHostClassName.packageName(), mFeatureHostTypeBuilder.build())
                .addFileComment("Featured code. Do not modify!")
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
        javaFile.writeTo(filer);
    }

}
