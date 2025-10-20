package com.maxacm.lr.controller;
import com.maxacm.lr.entity.Transaction;
import com.maxacm.lr.dto.NewTransaction;
import com.maxacm.lr.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewTransaction transaction,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.newtransaction(transaction, userDetails);
        return ResponseEntity.ok("Transaction created successfully");
    }


}
