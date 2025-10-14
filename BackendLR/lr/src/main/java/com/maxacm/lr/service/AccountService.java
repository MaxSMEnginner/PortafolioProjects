package com.maxacm.lr.service;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.AccountRepository;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.maxacm.lr.dto.NewAccount;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public Account newaccount(NewAccount newAccount, UserDetails userDetails){
        //Esto lo obtiene de la JWT
        String username = userDetails.getUsername();
        //Encontramos al usuario correspondiente
        User user= userRepository.findByUsername(username)
                .orElseThrow(()  -> new RuntimeException("User not found"));
        //CREAMOS UNA VARIABLE PARA VALIDAR EXISTENCIA DE CUENTA
        boolean exist= accountRepository.existsByNameAndTypeAndUser(
                newAccount.getName(),
                newAccount.getType(),
                user
        );
        //VALIDAR SI EXISTE LA CUENTA
        if (exist){
            throw new RuntimeException("The account already exists for this user");
        }
        //SI NO EXISTE SE CONSTRUYE LA CUENTA Y SE GUARDA EN LA BASE DE DATOS
        Account account = Account.builder()
                .name(newAccount.getName())
                .saldoActual(newAccount.getSaldoActual())
                .type(newAccount.getType())
                .user(user)
                .build();
        return accountRepository.save(account);
    }

}







