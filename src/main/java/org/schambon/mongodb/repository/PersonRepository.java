package org.schambon.mongodb.repository;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.schambon.mongodb.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Service
public class PersonRepository {

    @Autowired
    MongoClient mongoClient;

    public Person createNew(Person person) throws ParseException {
        MongoCollection<Person> people = mongoClient.getDatabase("app").getCollection("people", Person.class);
//        MongoCollection<Document> peopleAsDocument = mongoClient.getDatabase("app").getCollection("people");

        // generate an _id (of type ObjectId) for the Person
        person.setId(new ObjectId());

        //created now
        person.setCreated(new Date());
        people.insertOne(person);

//        Document doc = Document.parse("{'hello':'world', 'date':'2018-01-01T00:00:00'}");
//        doc.put("_id", new ObjectId());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-DDTHH:MM:SS");
//
//        doc.put("date", dateFormat.parse(doc.getString("date")));


//        peopleAsDocument.insertOne(doc);


        return person; // or throw a RuntimeException
    }

    public List<Person> all() {
        List<Person> result = new ArrayList<>();
        return mongoClient.getDatabase("app").getCollection("people", Person.class).find().into(result);
    }

    public List<Person> findByFirstName(String first) {
        return mongoClient.getDatabase("app").getCollection("people", Person.class).find(eq("first", first)).into(new ArrayList<>());
    }

    public Person findById(ObjectId id) {
        return mongoClient.getDatabase("app").getCollection("people", Person.class).find(eq("_id", id)).first();
    }
}
