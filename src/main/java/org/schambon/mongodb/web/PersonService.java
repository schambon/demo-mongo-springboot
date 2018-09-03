package org.schambon.mongodb.web;

import org.bson.types.ObjectId;
import org.schambon.mongodb.model.Person;
import org.schambon.mongodb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
public class PersonService {

    @Autowired
    PersonRepository repository;

    @RequestMapping(value = "/new", method = POST)
    public CreatePersonResponse insert(@RequestBody Person person) {
        return new CreatePersonResponse(repository.createNew(person).getId().toString());
    }

    @RequestMapping(value = "/", method = GET)
    public List<PersonResponse> getAll() {
        return repository.all().stream().map(PersonResponse::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/first/{first}", method = GET)
    public List<PersonResponse> byFirst(@PathVariable String first) {
        return repository.findByFirstName(first).stream().map(PersonResponse::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/id/{id}", method = GET)
    public PersonResponse byId(@PathVariable String id) {
        return new PersonResponse(repository.findById(new ObjectId(id)));
    }

    @RequestMapping(value = "/idraw/{id}", method = GET)
    public String getPersonRaw(@PathVariable String id) {
        return repository.findByIdRaw(id);
    }

    public static class PersonResponse { // old-school types might call that PersonDTO...
        String id;
        String first;
        String last;
        Date created;
        List<String> likes;

        private PersonResponse(Person person) {
            this.first = person.getFirst();
            this.last = person.getLast();
            this.created = person.getCreated();
            this.likes = person.getLikes();
            this.id = person.getId().toString();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public List<String> getLikes() {
            return likes;
        }

        public void setLikes(List<String> likes) {
            this.likes = likes;
        }
    }

    public static class CreatePersonResponse {
        String created;

        public CreatePersonResponse(String created) {
            this.created = created;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }
    }
}
