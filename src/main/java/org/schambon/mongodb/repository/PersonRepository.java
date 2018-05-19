package org.schambon.mongodb.repository;

import com.mongodb.ClientSessionOptions;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.session.ClientSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.schambon.mongodb.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Updates.*;

@Service
public class PersonRepository {

    private MongoCollection<Person> people;
    private MongoClient mongoClient;

    public PersonRepository(@Autowired MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.people = mongoClient.getDatabase("app").getCollection("people", Person.class);
    }

    public Person createNew(Person person) {

        // generate an _id (of type ObjectId) for the Person
        person.setId(new ObjectId());

        //created now
        person.setCreated(new Date());
        people.insertOne(person);

        return person; // or throw a RuntimeException
    }

    public List<Person> all() {
        List<Person> result = new ArrayList<>();
        return people.find().limit(1).into(result);
    }

    public List<Person> findByFirstName(String first) {
        return people.find(eq("first", first)).into(new ArrayList<>());
    }

    public Person findById(ObjectId id) {
        return people.find(eq("_id", id)).first();
    }

    /**
     * Add or subtract some amount to a Person's salary
     * @param personId
     * @param amount
     * @return true if one update has been done
     */
    public boolean addtoSalary(ObjectId personId, BigDecimal amount) {
        return people.updateOne(eq("_id", personId), inc("salary", amount)).getModifiedCount() == 1;
    }

    public boolean raisePercent(ObjectId personId, BigDecimal amountInPercent) {
        BigDecimal factor = amountInPercent.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
        return people.updateOne(eq("_id", personId), mul("salary", factor)).getModifiedCount() == 1;
    }

    /**
     * Add or subtract some amount to everybody's salary
     * @param amount
     * @return the number of people changed
     */
    public long generalRaise(BigDecimal amount) {
        return people.updateMany(new Document(), inc("salary", amount)).getModifiedCount();
    }

    public long generalRaisePercent(BigDecimal amountInPercent) {
        BigDecimal factor = amountInPercent.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
        return people.updateMany(new Document(), mul("salary", factor)).getModifiedCount();
    }

    /**
     * Set the minimum salary
     * @param minimum the new minimum salary
     * @return number of people brought up to minimum salary
     */
    public long setMinimumSalary(BigDecimal minimum) {
        return people.updateMany(new Document(), max("salary", minimum)).getModifiedCount();
    }

    /**
     * Flag people with salary higher than some amount, and return all rich people
     * For whatever reason we want to read from secondary, so we'll use a
     * causally consistent session
     */
    public List<Person> flagAndReturn(BigDecimal threshold) {

        try (ClientSession session = mongoClient.startSession(ClientSessionOptions.builder().causallyConsistent(true).build())) {
            people.updateMany(session, gte("salary", threshold), set("rich", true));
            ArrayList<Person> richFolks = people.withReadPreference(ReadPreference.secondary()).find(eq("rich", true)).into(new ArrayList<>());
            return richFolks;
        }

    }

}
