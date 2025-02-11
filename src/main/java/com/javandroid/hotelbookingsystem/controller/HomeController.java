package com.javandroid.hotelbookingsystem.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage() {
        return "index"; // Returns index.html inside templates folder
    }
}
