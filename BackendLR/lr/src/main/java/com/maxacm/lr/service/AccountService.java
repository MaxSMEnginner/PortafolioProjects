package com.maxacm.lr.service;

import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.AccountRepository;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.maxacm.lr.dto.NewAccount;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public Account newaccount(NewAccount newAccount){
        if (accountRepository.existsByNameAndType(newAccount.getName(), newAccount.getType())) {
            throw new RuntimeException("The account already exists");
        }


        User user = userRepository.findById(newAccount.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + newAccount.getUserId()));


        Account account = Account.builder()
                .name(newAccount.getName())
                .saldoActual(newAccount.getSaldoActual())
                .type(newAccount.getType())
                .user(user)
                .build();

        return accountRepository.save(account);
    }

}







