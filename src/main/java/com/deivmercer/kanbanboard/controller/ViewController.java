package com.deivmercer.kanbanboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {

    @GetMapping(path = "/")
    public ModelAndView index() {

        return new ModelAndView("index.html");
    }

    @GetMapping(path = "/login")
    public ModelAndView login() {

        return new ModelAndView("login.html");
    }

    @GetMapping(path = "/signup")
    public ModelAndView signup() {

        return new ModelAndView("signup.html");
    }
}
