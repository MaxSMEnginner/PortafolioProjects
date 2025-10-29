package com.maxacm.lr.controller.transaction;
import com.maxacm.lr.dto.transactions.NewTransaction;
import com.maxacm.lr.repository.transactions.TransactionRepository;
import com.maxacm.lr.service.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewTransaction transaction,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.newtransaction(transaction, userDetails);
        return ResponseEntity.ok("Transaction created successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }



}
