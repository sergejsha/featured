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

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import de.halfbit.featured.FeatureEvent;
import de.halfbit.featured.compiler.model.FeatureNode;
import de.halfbit.featured.compiler.model.MethodNode;
import de.halfbit.featured.compiler.model.ModelNode;
import de.halfbit.featured.compiler.model.ParameterNode;

@AutoService(Processor.class)
public final class FeatureProcessor extends AbstractProcessor {

    private Filer mFiler;

    private FeatureModelValidator mFeatureValidator;
    private Names mNames;

    @Override public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFiler = processingEnv.getFiler();
        mNames = new Names(processingEnv.getElementUtils());
        mFeatureValidator = new FeatureModelValidator(processingEnv.getMessager(), mNames);
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(FeatureEvent.class.getCanonicalName());
        return types;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public boolean process(Set<? extends TypeElement> anns, RoundEnvironment env) {

        ModelNode model = new ModelNode();

        // process each @FeatureEvent element
        for (Element element : env.getElementsAnnotatedWith(FeatureEvent.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;

            try {

                TypeElement parentElement = (TypeElement) element.getEnclosingElement();
                String featureName = parentElement.getQualifiedName().toString();

                FeatureNode featureNode = model.getFeatureNode(featureName);
                if (featureNode == null) {
                    featureNode = new FeatureNode(featureName, parentElement);
                    model.putFeatureNode(featureNode);
                }

                ExecutableElement executableElement = (ExecutableElement) element;
                Name methodName = executableElement.getSimpleName();

                if (!featureNode.hasMethod(methodName)) {
                    MethodNode methodNode = new MethodNode(executableElement, featureNode);
                    featureNode.addMethod(methodName, methodNode);

                    List<? extends VariableElement> params = executableElement.getParameters();
                    for (int i = 0, size = params.size(); i < size; i++) {
                        VariableElement param = params.get(i);
                        methodNode.addParameter(
                                new ParameterNode(methodNode)
                                        .setName(param.getSimpleName().toString())
                                        .setType(mNames.getTypeNameByKind(param))
                                        .setDispatchCompleted(false));
                    }

                    if (methodNode.hasDispatchCompletedParameter()) {
                        methodNode.addParameter(
                                new ParameterNode(methodNode)
                                        .setName("onDispatchCompleted")
                                        .setType(mNames.getDispatchCompletedClassName())
                                        .setDispatchCompleted(true));
                    }
                }

            } catch (Exception e) {
                mFeatureValidator.error(element, e.getMessage());
            }
        }

        // Add feature nodes coming from library. They are not annotated but must be
        // present in the model for proper generation of feature hosts.
        model.detectLibraryFeatures(processingEnv, mNames);

        // enhance model
        model.detectInheritance(processingEnv);

        // validate model nodes
        model.accept(mFeatureValidator);

        // generate source code
        FeatureCodeBrewer featureBrewer = new FeatureCodeBrewer(mNames);
        Collection<FeatureNode> featureNodes = model.getFeatureNodes();
        for (FeatureNode featureNode : featureNodes) {
            if (featureNode.isValid() && !featureNode.isLibraryNode()) {
                featureNode.accept(featureBrewer);
                try {
                    featureBrewer.brewTo(mFiler);
                } catch (IOException e) {
                    TypeElement element = featureNode.getElement();
                    mFeatureValidator.error(element,
                            "Unable to generate code for type %s: %s", element, e.getMessage());
                }
            }
        }

        return true;
    }

}
