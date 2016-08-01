package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.api.dataset.location.SemanticDomain;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.type.Type;

public class ColumnMetadata {

    private final Quality quality = new Quality();

    private String id;

    private String name;

    private Type type;

    private final Statistics statistics = new Statistics();

    /** Domain of the column. Rows may have more specific semantic domain. **/
    private SemanticDomain semanticDomain;

    /** if the domain has been changed/forced manually by the user */
    private boolean domainForced;

    /** if the type has been changed/forced manually by the user */
    private boolean typeForced;

    /**
     * Default empty constructor.
     */
    public ColumnMetadata() {
        // no op
    }

    /**
     * Create a column metadata from the given parameters.
     *
     * @param name the column name.
     * @param typeName the column type.
     */
    private ColumnMetadata(String name, Type typeName) {
        this.name = name;
        this.type = type;
    }

    public Quality getQuality() {
        return quality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public SemanticDomain getSemanticDomain() {
        return semanticDomain;
    }

    public void setSemanticDomain(SemanticDomain semanticDomain) {
        this.semanticDomain = semanticDomain;
    }

    public boolean isDomainForced() {
        return domainForced;
    }

    public void setDomainForced(boolean domainForced) {
        this.domainForced = domainForced;
    }

    public boolean isTypeForced() {
        return typeForced;
    }

    public void setTypeForced(boolean typeForced) {
        this.typeForced = typeForced;
    }
}
