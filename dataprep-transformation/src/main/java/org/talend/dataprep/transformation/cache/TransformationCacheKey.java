//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.transformation.cache;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.cache.ContentCacheKey;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Content cache key used to cache transformation.
 */
public class TransformationCacheKey implements ContentCacheKey {

    /** Format parameters (if any, if none, default to empty string) */
    private final String parameters;

    /** The dataset id. */
    private String datasetId;

    /** The dataset metadata hash. */
    private String datasetMetadataHash;

    /** The preparation id. */
    private String preparationId;

    /** The optional step id. */
    private String stepId;

    /** The transformation format. */
    private String format;

    /**
     * Create a content cache for this transformation.
     *
     * @param preparationId the preparation id.
     * @param metadata the dataset metadata.
     * @param format the transformation format.
     * @param stepId the preparation version (step).
     * @throws IOException if an error occurs while computing the cache key.
     */
    public TransformationCacheKey(String preparationId, DataSetMetadata metadata, String format, String stepId)
            throws IOException {
        this(preparationId, metadata, format, StringUtils.EMPTY, stepId);
    }

    /**
     * Create a content cache key that only matches the given dataset id.
     *
     * @param preparationId the preparation id.
     * @param metadata the dataset metadata.
     * @param format the transformation format.
     * @param stepId the preparation version (step).
     * @throws IOException if an error occurs while computing the cache key.
     */
    public TransformationCacheKey(String preparationId, DataSetMetadata metadata, String format, String parameters, String stepId)
            throws IOException {
        if (StringUtils.equals("head", stepId)) {
            throw new IllegalArgumentException("'head' is not allowed as step id for cache key");
        }
        this.preparationId = preparationId;
        this.datasetId = metadata.getId();
        this.datasetMetadataHash = hash(metadata);
        this.format = format;
        this.stepId = stepId;
        this.parameters = parameters;
    }

    /**
     * @param metadata the dataset metadata.
     * @return the md5 hash of the given dataset metadata.
     * @throws IOException if an error occurs while computing the cache key.
     */
    private String hash(DataSetMetadata metadata) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writer().writeValueAsString(metadata);
        return DigestUtils.sha1Hex(json.getBytes("UTF-8"));
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "TransformationCacheKey{" + "datasetId='" + datasetId + '\'' + ", datasetMetadataHash='" + datasetMetadataHash
                + '\'' + ", preparationId='" + preparationId + '\'' + ", stepId='" + stepId + '\''
                + ", format='" + format + '\'' + '}';
    }

    /**
     * The key must be unique per content !
     *
     * @return the key for this cache content as a string.
     */
    @Override
    public String getKey() {
        return "transformation-" + Objects.hash(preparationId, datasetId, datasetMetadataHash, stepId, format, parameters);
    }
}
