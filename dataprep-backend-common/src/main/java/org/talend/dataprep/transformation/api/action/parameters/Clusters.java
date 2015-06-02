package org.talend.dataprep.transformation.api.action.parameters;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Clusters {

    private final List<String> titles = new ArrayList<>(2);

    private final List<ClusterItem> clusterItems = new ArrayList<>();

    Clusters(List<String> titles, final List<ClusterItem> clusters) {
        this.titles.addAll(titles);
        this.clusterItems.addAll(clusters);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<ClusterItem> getClusters() {
        return clusterItems;
    }

    public static class Builder {

        private final List<String> titles = new ArrayList<>(2);

        private final List<ClusterItem.Builder> clusters = new ArrayList<>();

        public Builder title(final String title) {
            this.titles.add(title);
            return this;
        }

        public Builder cluster(final ClusterItem.Builder cluster) {
            this.clusters.add(cluster);
            return this;
        }

        public Clusters build() {
            final List<ClusterItem> items = this.clusters.stream().map(ClusterItem.Builder::build).collect(toList());
            return new Clusters(titles, items);
        }
    }
}
