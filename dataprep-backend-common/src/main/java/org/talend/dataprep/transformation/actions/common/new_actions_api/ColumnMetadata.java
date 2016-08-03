package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.Quality;
import org.talend.dataprep.api.dataset.location.SemanticDomain;
import org.talend.dataprep.api.dataset.statistics.Statistics;
import org.talend.dataprep.api.type.Type;

/**
 * Exposure of column metadata for actions.
 * Actions should be able to modify:
 * <ul>
 *     <li>column name</li>
 *     <li>column type => that makes it forced</li>
 *     <li>column semantic domain => that makes it forced</li>
 * </ul>
 * And should be able to read:
 * <ul>
 *     <li>column id</li>
 *     <li>column name</li>
 *     <li>column type</li>
 *     <li>if the column type is forced</li>
 *     <li>column semantic domain</li>
 *     <li>if the column domain is forced</li>
 *     <li>column quality data</li>
 *     <li>column statistics</li>
 * </ul>
 *
 */
public class ColumnMetadata {

    private String id;

    private String name;

    private Type type;

    private final Quality quality = new Quality();

    private final Statistics statistics = new Statistics();

    /** Domain of the column. Rows may have more specific semantic domain. **/
    private SemanticDomain semanticDomain;

    /** if the domain has been changed/forced manually by the user */
    private boolean domainForced;

    /** if the type has been changed/forced manually by the user */
    private boolean typeForced;

    public ColumnMetadata() {}

    private ColumnMetadata(String name, Type type) {
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
