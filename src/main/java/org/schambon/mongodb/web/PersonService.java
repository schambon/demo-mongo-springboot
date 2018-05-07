package org.schambon.mongodb.web;

import org.schambon.mongodb.model.Person;
import org.schambon.mongodb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonService {

    @Autowired
    PersonRepository repository;

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public void insert(@RequestBody Person person) {
        repository.createNew(person);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Person> getAll() {
        return repository.all();
    }

    @RequestMapping(value = "/first/{first}", method = RequestMethod.GET)
    public Person byFirst(@PathVariable String first) {
        return repository.findByFirstName(first);
    }
}
