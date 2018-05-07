package org.schambon.mongodb.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.schambon.mongodb.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Service
public class PersonRepository {

    @Autowired
    MongoClient mongoClient;

    public void createNew(Person person) {
        MongoCollection<Person> people = mongoClient.getDatabase("app").getCollection("people", Person.class);
        //created now
        person.setCreated(new Date());
        people.insertOne(person);
    }

    public List<Person> all() {
        List<Person> result = new ArrayList<>();
        return mongoClient.getDatabase("app").getCollection("people", Person.class).find().into(result);
    }

    public Person findByFirstName(String first) {
        return mongoClient.getDatabase("app").getCollection("people", Person.class).find(eq("first", first)).first();
    }
}
