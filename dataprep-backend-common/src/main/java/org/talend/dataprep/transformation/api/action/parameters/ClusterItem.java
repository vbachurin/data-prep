package org.talend.dataprep.transformation.api.action.parameters;

import java.util.ArrayList;
import java.util.List;

public class ClusterItem {
    private final List<Parameter> parameters = new ArrayList<>();
    private final Parameter replace;

    public ClusterItem(final List<Parameter> parameters, final Parameter replace) {
        this.parameters.addAll(parameters);
        this.replace = replace;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Parameter getReplace() {
        return replace;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Parameter> parameters = new ArrayList<>();
        private Parameter replace;

        public Builder parameter(final Parameter parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Builder replace(final Parameter replace) {
            this.replace = replace;
            return this;
        }

        public ClusterItem build() {
            return new ClusterItem(parameters, replace);
        }
    }
}
