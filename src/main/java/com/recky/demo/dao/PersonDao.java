package com.recky.demo.dao;

import java.util.UUID;

import com.recky.demo.model.Person;

public interface PersonDao {

    int insertperson(UUID id, Person person);

    default int insertperson (Person person)
    {
        UUID id = UUID.randomUUID();
        return insertperson(id, person);
    }
    

}
