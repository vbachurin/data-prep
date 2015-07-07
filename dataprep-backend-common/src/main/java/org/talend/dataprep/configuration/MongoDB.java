package org.talend.dataprep.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Configuration for MongoDB.
 */
@Configuration
@ConditionalOnProperty(name = "preparation.store", havingValue = "mongodb")
public class MongoDB {

    /** Value to use to replace the '.' in Mongo key map. */
    private static final String REPLACEMENT = "\\_dot_";

    /** The Mongo db factory. */
    @Autowired
    private MongoDbFactory mongoDbFactory;

    /**
     * @see MappingMongoConverter#setMapKeyDotReplacement(String)
     * @return the mapping Mongo converter to prevent '.' in key maps for mongo.
     */
    @Bean
    public MappingMongoConverter getMappingMongoConverter() {

        MongoMappingContext mappingContext = new MongoMappingContext();
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        mongoConverter.setMapKeyDotReplacement(REPLACEMENT);
        return mongoConverter;
    }

}
