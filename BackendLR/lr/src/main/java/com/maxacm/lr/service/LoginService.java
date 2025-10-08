//package com.maxacm.lr.service;
//import com.maxacm.lr.entity.User;
//import com.maxacm.lr.dto.LoginRequest;
//import com.maxacm.lr.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import lombok.extern.slf4j.Slf4j;
//
//import java.sql.Array;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LoginService {
//    private final UserRepository userRepository;
//
//    public ArrayList<String> login(String username, String password){
//        log.info("Respuesta: "+ userRepository.existsByUsername(username));
//        if (userRepository.existsByUsername(username)){
//
//            log.info(username);
//        }
//        ArrayList<String> datos = new ArrayList<>(Arrays.asList(username, password));
//        return datos;
//    }
//
//}
