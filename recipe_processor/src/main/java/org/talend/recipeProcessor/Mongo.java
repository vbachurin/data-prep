package org.talend.recipeProcessor;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Mongo {// implements RecipeProcessor {

    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("test");
        Set<String> colls = db.getCollectionNames();

        for (String s : colls) {
            System.out.println(s);
        }
        DBCollection coll = db.getCollection("preparation_step1");

        BasicDBObject updateQuery = new BasicDBObject().append("$toUpper", "city");

        coll.update(new BasicDBObject(), updateQuery, false, true);
    }

}
