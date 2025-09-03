package com.maxsoft.loginregister.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeControlador {

    @GetMapping
    public String home(){
        return "Bienvenido al Home Usuario 🚀";
    }
}
