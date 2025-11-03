package com.maxacm.lr.controller.transaction;
import com.maxacm.lr.dto.transactions.NewTransaction;
import com.maxacm.lr.dto.transactions.TransactionDTO;
import com.maxacm.lr.dto.transactions.UpdateTransaction;
import com.maxacm.lr.entity.Transaction;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.transactions.TransactionRepository;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.service.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewTransaction transaction,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.newtransaction(transaction, userDetails);
        return ResponseEntity.ok("Transaction created successfully");
    }

//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        if (transactionRepository.existsById(id)) {
//            transactionRepository.deleteById(id);
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.notFound().build();
//    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<TransactionDTO> update(@PathVariable Long id, @RequestBody UpdateTransaction dto) {
        try{
            Transaction updatetransaction= transactionService.updateTransaction(id, dto);
            return ResponseEntity.ok(transactionService.toDTO(updatetransaction));
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/transactions")
    public List<TransactionDTO> getAllTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUser(user).stream()
                .map(transactionService::toDTO)  // ✅ Usa el método toDTO actualizado
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> findById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findById(id)
                .filter(category -> category.getUser().getId().equals(user.getId()))
                .map(transactionService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
