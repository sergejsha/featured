package de.halfbit.featured.compiler;

import de.halfbit.featured.compiler.model.FeatureNode;

public class Assertions {

    private Assertions() {
    }

    public static <S> S assertNotNull(S subj, FeatureNode featureNode) {
        if (subj == null) {
            throw new IllegalArgumentException("Subject is null in " + featureNode);
        }
        return subj;
    }

}
