package com.recky.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // Base URL for the controller
public class HelloController {

    @GetMapping
    public String getHelloMessage() {
        return "Hello, World!";
    }
    // @Autowired
    // private UserRepo userRepo;

    // @GetMapping("/users")
    // public List<?> getUsers() {
    // return userRepo.findAll();
    // }

    // @PostMapping("/users")
    // public String saveUser() {
    // // Add logic to save a user
    // return "User saved successfully!";
    // }
}
