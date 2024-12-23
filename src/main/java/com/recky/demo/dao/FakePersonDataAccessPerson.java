package com.recky.demo.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.recky.demo.model.Person;

// @Component
@Repository
public class FakePersonDataAccessPerson implements PersonDao {

    private static List<Person> Db = new ArrayList<>();

    @Override
    public int insertperson(UUID id, Person person) {
        Db.add(new Person(id,person.getName()));
        return 1;
    }
}
