package org.talend.dataprep.transformation.api.action.dynamic.cluster;

import static org.talend.dataprep.api.type.Type.BOOLEAN;
import static org.talend.dataprep.api.type.Type.STRING;

import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.api.action.dynamic.DynamicParameters;
import org.talend.dataprep.transformation.api.action.parameters.ClusterItem;
import org.talend.dataprep.transformation.api.action.parameters.Clusters;
import org.talend.dataprep.transformation.api.action.parameters.GenericParameter;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Cluster action dynamic parameter generator
 * It takes an InputStream as argument, containing the dataset
 */
@Component
public class ClusterParameters implements DynamicParameters {
    @Override
    public GenericParameter getParameters(final String columnId, final InputStream content) {
        //Call DQ cluster service
        /*
        {
            "clusters":[
            {
                "score":0.8,
                    "suggested value":"Black T-shirt",
                    "values":[
                        "Black T-shirt",
                        "Bck T-shirt",
                        "Blck tshirt"
                ]
            },
            {
                "score":0.4,
                    "suggested value":"White T-shirt",
                    "values":[
                        "White T-shirt",
                        "Wt T-shirt",
                        "White t shirt"
                ]
            }
            ]
        }
        */
        
        final Clusters clusters = Clusters.builder()
                .title("We found these values") //TODO JSO : externalize with internationalization
                .title("And we'll keep this value")
                .cluster(ClusterItem.builder() //TODO JSO : convert DQ result to those builders
                                .parameter(new Parameter("Texa", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Tixass", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Tex@s", BOOLEAN.getName(), "true"))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Texas"))
                )
                .cluster(ClusterItem.builder()
                                .parameter(new Parameter("Massachusetts", BOOLEAN.getName(), "false"))
                                .parameter(new Parameter("Masachusetts", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Massachussetts", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Massachusets", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Masachussets", BOOLEAN.getName(), "true"))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Massachussets"))
                )
                .cluster(ClusterItem.builder()
                                .parameter(new Parameter("Tata", BOOLEAN.getName(), "false"))
                                .parameter(new Parameter("tata", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Tataaa", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("tatoa", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Taatao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Taatao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Tootao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("Taotao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("taotao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("t@tao", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("t@t@", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("t@T@", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("t@t@@", BOOLEAN.getName(), "true"))
                                .parameter(new Parameter("T@T@", BOOLEAN.getName(), "true"))
                                .replace(new Parameter("replaceValue", STRING.getName(), "Tata"))
                )
                .build();

        return new GenericParameter("cluster", clusters);
    }
}
