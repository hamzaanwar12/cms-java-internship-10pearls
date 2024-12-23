package com.recky.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recky.demo.model.PrevUser;

public interface UserRepo extends JpaRepository<PrevUser, Long> {
}
