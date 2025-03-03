package com.recky.demo.api;

import com.recky.demo.model.Person;
import com.recky.demo.service.PersonService;

public class PersonController {

    private final PersonService personService;
    public PersonController(PersonService personService)
    {
        this.personService = personService;
    }

    public void addPerson(Person person)
    {
        personService.addPerson(person);
    }

}
