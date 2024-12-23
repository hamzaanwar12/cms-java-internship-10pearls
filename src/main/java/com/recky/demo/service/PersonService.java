package com.recky.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.PersonDao;
import com.recky.demo.model.Person;


@Service
public class PersonService {
    private final PersonDao persondao;

    @Autowired
    public PersonService(PersonDao personDao)
    {
        this.persondao = personDao;
    }
    
    public int addPerson(Person person)
    {
        return persondao.insertperson(person);
    }

}
