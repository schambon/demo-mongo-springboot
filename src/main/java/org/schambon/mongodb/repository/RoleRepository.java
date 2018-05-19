package org.schambon.mongodb.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Roles are free-form: the application doesn't have strong rules over what fields there are. So we work
 * with Maps.
 */
@Service
public class RoleRepository {

    MongoCollection<Document> collection;

    public RoleRepository(@Autowired MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("app").getCollection("roles");
    }

    public Map<String, Object> getRoleInfo(String roleId, List<String> fields) {

        FindIterable<Document> query = collection.find(eq("_id", roleId));
        if (fields != null && fields.size() > 0) {
            query = query.projection(include(fields));
        }

        MongoCursor<Document> cursor = query.iterator();
        if (cursor.hasNext()) {
            Document doc = cursor.next();
            doc.remove("_id");
            return doc;
        } else {
            return null; // or throw NoSuchRoleException?
        }
    }

    /**
     * get all role attributes
     */
    public Map<String, Object> getRoleInfo(String roleId) {
        return getRoleInfo(roleId, null);
    }

    public String createRole(String roleId, Map<String, Object> fields) {
        Document doc = new Document("_id", roleId);
        doc.putAll(fields);

        collection.insertOne(doc);
        return roleId;
    }

    /**
     * Update some fields in a role
     * @param payload
     */
    public void updateRoleFields(String roleId, Map<String, Object> payload) {
        collection.updateOne(eq("_id", roleId),
                combine(payload.entrySet().stream().map(entry -> set(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())));

        // alternative without using Updates helpers:
        // collection.updateOne(eq("_id", roleId), new Document("$set", new Document(payload));
    }
}
