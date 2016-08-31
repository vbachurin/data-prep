package org.talend.dataprep.transformation.cache;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.export.ExportParameters;
import org.talend.dataprep.cache.ContentCacheKey;
import org.talend.dataprep.security.Security;

import static org.talend.dataprep.api.export.ExportParameters.SourceType.HEAD;

/**
 * Generate cache key
 */
@Component
public class CacheKeyGenerator {
    @Autowired
    private Security security;

    /**
     * Build a cache key to identify the transformation result content
     */
    public TransformationCacheKey generateContentKey(final String datasetId, final String preparationId,
                                                     final String stepId, final String format,
                                                     final ExportParameters.SourceType sourceType) {
        return this.generateContentKey(
                datasetId,
                preparationId,
                stepId,
                format,
                sourceType,
                null
        );
    }

    /**
     * Build a cache key with additional parameters
     * When source type is HEAD, the user id is not included in cache key, as the HEAD sample is common for all users
     */
    public TransformationCacheKey generateContentKey(final String datasetId, final String preparationId,
                                                     final String stepId, final String format,
                                                     final ExportParameters.SourceType sourceType,
                                                     final String parameters) {
        final String actualParameters = parameters == null ? StringUtils.EMPTY : parameters;
        final ExportParameters.SourceType actualSourceType = sourceType == null ? HEAD : sourceType;
        final String actualUserId = actualSourceType == HEAD ? null : getUser();

        return new TransformationCacheKey(
                preparationId,
                datasetId,
                format,
                stepId,
                actualParameters,
                actualSourceType,
                actualUserId
        );
    }

    /**
     * Build a metadata cache key to identify the transformation result content
     * When source type is HEAD, the user id is not included in cache key, as the HEAD sample is common for all users
     */
    public TransformationMetadataCacheKey generateMetadataKey(final String preparationId, final String stepId, final ExportParameters.SourceType sourceType) {
        final ExportParameters.SourceType actualSourceType = sourceType == null ? HEAD : sourceType;
        final String actualUserId = actualSourceType == HEAD ? null : getUser();

        return new TransformationMetadataCacheKey(
                preparationId,
                stepId,
                actualSourceType,
                actualUserId
        );
    }

    private String getUser() {
        return security.getUserId();
    }
}
