package de.halfbit.featured.compiler.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ModelNode {

    private final Map<String, FeatureNode> mFeatureNodes;

    public ModelNode() {
        mFeatureNodes = new LinkedHashMap();
    }

    public FeatureNode getFeatureNode(String featureName) {
        return mFeatureNodes.get(featureName);
    }

    public void setFeatureNode(String featureName, FeatureNode featureNode) {
        mFeatureNodes.put(featureName, featureNode);
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

    public void detectInheritance(Types typeUtils) {
        Collection<FeatureNode> featureNodes = getFeatureNodes();
        for (FeatureNode featureNode : featureNodes) {
            TypeMirror superType = featureNode.getElement().getSuperclass();
            while (superType.getKind() != TypeKind.NONE) {
                for (FeatureNode otherFeatureNode : featureNodes) {
                    if (featureNode == otherFeatureNode) {
                        continue;
                    }
                    Name otherName = otherFeatureNode.getElement().getQualifiedName();
                    Name superName = ((TypeElement) typeUtils.asElement(superType)).getQualifiedName();
                    if (otherName.equals(superName)) {
                        featureNode.setSuperFeatureNode(otherFeatureNode);
                    }
                }
                superType = ((TypeElement) typeUtils.asElement(superType)).getSuperclass();
            }
        }
    }

}
