package com.yesnault.sag.controller;

import com.yesnault.sag.model.User;
import com.yesnault.sag.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class UsersController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/sites", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<User> list() {
        return new ArrayList<User>(userRepository.findAll());
    }

    @RequestMapping(value = "/sites/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    User getById(@PathVariable long id) {
        return userRepository.findOne(id);
    }

    @RequestMapping(value = "/sites/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody User user) {
        long id = user.getId();
        userRepository.save(user);
    }

    @RequestMapping(value = "/sites/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody User user) {
        userRepository.save(user);
    }

    @RequestMapping(value = "/sites/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        userRepository.delete(id);
    }

}