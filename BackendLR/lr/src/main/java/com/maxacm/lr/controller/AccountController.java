package com.maxacm.lr.controller;
import com.maxacm.lr.entity.Account;
import com.maxacm.lr.dto.NewAccount;
import com.maxacm.lr.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<NewAccount> create(@RequestBody NewAccount account) {
        Account saved = accountService.newaccount(account);
        return ResponseEntity.ok(account);
    }


}
