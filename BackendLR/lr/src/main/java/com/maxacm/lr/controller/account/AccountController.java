package com.maxacm.lr.controller.account;
import com.maxacm.lr.dto.accounts.NewAccount;
import com.maxacm.lr.service.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<String> create(@RequestBody NewAccount account,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        accountService.newaccount(account, userDetails);
        return ResponseEntity.ok("Account created successfully");
    }


}
