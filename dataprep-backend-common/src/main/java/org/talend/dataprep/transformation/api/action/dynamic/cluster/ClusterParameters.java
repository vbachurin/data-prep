package org.talend.dataprep.transformation.api.action.dynamic.cluster;

import static org.talend.dataprep.api.type.Type.BOOLEAN;
import static org.talend.dataprep.api.type.Type.STRING;

import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.talend.dataprep.i18n.MessagesBundle;
import org.talend.dataprep.transformation.api.action.dynamic.DynamicParameters;
import org.talend.dataprep.transformation.api.action.parameters.ClusterItem;
import org.talend.dataprep.transformation.api.action.parameters.Clusters;
import org.talend.dataprep.transformation.api.action.parameters.GenericParameter;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Cluster action dynamic parameter generator It takes an InputStream as argument, containing the dataset
 */
@Component
public class ClusterParameters implements DynamicParameters {

    @Override
    public GenericParameter getParameters(final String columnId, final InputStream content) {
        // Call DQ cluster service
        /*
         * { "clusters":[ { "score":0.8, "suggested value":"Black T-shirt", "values":[ "Black T-shirt", "Bck T-shirt",
         * "Blck tshirt" ] }, { "score":0.4, "suggested value":"White T-shirt", "values":[ "White T-shirt",
         * "Wt T-shirt", "White t shirt" ] } ] }
         */

        final Clusters clusters = Clusters
                .builder()
                .title(MessagesBundle.getString("parameter.textclustering.title.1"))
                .title(MessagesBundle.getString("parameter.textclustering.title.2"))
                .cluster(
                        ClusterItem
                                .builder()
                                // TODO JSO : convert DQ result to those builders
                                .parameter(new Parameter("Texa", BOOLEAN.getName()))
                                // TODO JSO : remove default
                                .parameter(new Parameter("Tixass", BOOLEAN.getName()))
                                .parameter(new Parameter("Tex@s", BOOLEAN.getName()))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Texas")))
                .cluster(
                        ClusterItem.builder().parameter(new Parameter("Massachusetts", BOOLEAN.getName()))
                                .parameter(new Parameter("Masachusetts", BOOLEAN.getName()))
                                .parameter(new Parameter("Massachussetts", BOOLEAN.getName()))
                                .parameter(new Parameter("Massachusets", BOOLEAN.getName()))
                                .parameter(new Parameter("Masachussets", BOOLEAN.getName()))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Massachussets")))
                .cluster(
                        ClusterItem.builder().parameter(new Parameter("Tata", BOOLEAN.getName()))
                                .parameter(new Parameter("tata", BOOLEAN.getName()))
                                .parameter(new Parameter("Tataaa", BOOLEAN.getName()))
                                .parameter(new Parameter("tatoa", BOOLEAN.getName()))
                                .parameter(new Parameter("Taatao", BOOLEAN.getName()))
                                .parameter(new Parameter("Taatao", BOOLEAN.getName()))
                                .parameter(new Parameter("Tootao", BOOLEAN.getName()))
                                .parameter(new Parameter("Taotao", BOOLEAN.getName()))
                                .parameter(new Parameter("taotao", BOOLEAN.getName()))
                                .parameter(new Parameter("t@tao", BOOLEAN.getName()))
                                .parameter(new Parameter("t@t@", BOOLEAN.getName()))
                                .parameter(new Parameter("t@T@", BOOLEAN.getName()))
                                .parameter(new Parameter("t@t@@", BOOLEAN.getName()))
                                .parameter(new Parameter("T@T@", BOOLEAN.getName()))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Tata"))).build();

        return new GenericParameter("cluster", clusters);
    }
}
