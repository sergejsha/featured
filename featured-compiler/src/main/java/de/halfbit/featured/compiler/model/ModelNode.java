package de.halfbit.featured.compiler.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import de.halfbit.featured.compiler.Names;

public class ModelNode {

    private final Map<String, FeatureNode> mFeatureNodes;

    public ModelNode() {
        mFeatureNodes = new LinkedHashMap();
    }

    public FeatureNode getFeatureNode(String featureName) {
        return mFeatureNodes.get(featureName);
    }

    public void putFeatureNode(FeatureNode featureNode) {
        mFeatureNodes.put(featureNode.getName(), featureNode);
    }

    public void accept(ModelNodeVisitor visitor) {
        Collection<FeatureNode> featureNodes = getFeatureNodes();
        for (FeatureNode featureNode : featureNodes) {
            featureNode.accept(visitor);
        }
    }

    public Collection<FeatureNode> getFeatureNodes() {
        return mFeatureNodes.values();
    }

    public void detectInheritance(ProcessingEnvironment env) {
        Collection<FeatureNode> featureNodes = getFeatureNodes();
        for (FeatureNode featureNode : featureNodes) {

            // feature has inheriting features if it is parametried with generics
            List<? extends TypeParameterElement> typeParams = featureNode.getElement().getTypeParameters();
            if (typeParams.size() > 0) {
                featureNode.setHasInheritingFeatureNodes(true);
                continue;
            }

            // look up for super-features whithin collected nodes
            TypeMirror superType = featureNode.getElement().getSuperclass();
            while (superType.getKind() != TypeKind.NONE) {
                for (FeatureNode otherFeatureNode : featureNodes) {
                    if (featureNode == otherFeatureNode) {
                        continue;
                    }
                    Name otherName = otherFeatureNode.getElement().getQualifiedName();
                    Name superName = ((TypeElement) env.getTypeUtils().asElement(superType)).getQualifiedName();
                    if (otherName.equals(superName)) {
                        featureNode.setSuperFeatureNode(otherFeatureNode);
                        break;
                    }
                }
                superType = ((TypeElement) env.getTypeUtils().asElement(superType)).getSuperclass();
            }
        }
    }

    public void detectLibraryFeatures(ProcessingEnvironment env, Names names) {
        Collection<FeatureNode> featureNodes = new ArrayList<>(getFeatureNodes());
        for (FeatureNode featureNode : featureNodes) {
            TypeMirror superType = featureNode.getElement().getSuperclass();
            if (superType.getKind() == TypeKind.NONE) {
                continue;
            }

            FeatureNode superNode = findFetureNode(superType, env.getTypeUtils());
            if (superNode != null) {
                // node already exists
                continue;
            }

            TypeMirror featureType = env.getElementUtils().getTypeElement(
                    names.getFeatureClassName().toString()).asType();

            if (env.getTypeUtils().isSameType(superType, featureType)) {
                continue;
            }

            if (env.getTypeUtils().isSubtype(superType, featureType)) {
                // create library node
                TypeElement superTypeElement = (TypeElement) env.getTypeUtils().asElement(superType);
                String name = superTypeElement.getQualifiedName().toString();
                FeatureNode node = new FeatureNode(name, superTypeElement);
                node.setLibraryNode(true);
                putFeatureNode(node);
            }
        }
    }

    private FeatureNode findFetureNode(TypeMirror featureType, Types types) {
        String featureName = ((TypeElement) types.asElement(featureType))
                .getQualifiedName().toString();
        return mFeatureNodes.get(featureName);
    }

}
