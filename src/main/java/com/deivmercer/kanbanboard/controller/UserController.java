package com.deivmercer.kanbanboard.controller;

import com.deivmercer.kanbanboard.factory.UserFactory;
import com.deivmercer.kanbanboard.model.User;
import com.deivmercer.kanbanboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/signUp")
    public ResponseEntity<String> signUp(@RequestParam String username, @RequestParam String password) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent())
            return new ResponseEntity<>("Username already existing.", HttpStatus.BAD_REQUEST);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User userEntity = UserFactory.getUser(username, bCryptPasswordEncoder.encode(password));
        userRepository.save(userEntity);
        return new ResponseEntity<>("User created.", HttpStatus.CREATED);
    }

    @PostMapping(path = "/logIn")
    public ResponseEntity<String> logIn(@RequestParam String username, @RequestParam String password) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && bCryptPasswordEncoder.matches(password, user.get().getPassword()))
            return new ResponseEntity<>("Login successful.", HttpStatus.OK);
        return new ResponseEntity<>("Login failed.", HttpStatus.UNAUTHORIZED);
    }
}
