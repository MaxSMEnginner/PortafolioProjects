package com.maxacm.lr.controller.account;
import com.maxacm.lr.dto.accounts.AccountDTO;
import com.maxacm.lr.dto.accounts.NewAccount;
import com.maxacm.lr.dto.accounts.UpdateAccount;
import com.maxacm.lr.dto.users.UserDTO;
import com.maxacm.lr.entity.Account;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.accounts.AccountRepository;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.service.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewAccount account,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        accountService.newaccount(account, userDetails);
        return ResponseEntity.ok("Account created successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<AccountDTO> update(@PathVariable Long id, @RequestBody UpdateAccount dto) {
        try{
            Account updateaccount= accountService.updateAccount(id, dto);
            return ResponseEntity.ok(accountService.toDTO(updateaccount));
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }



    @GetMapping("/accounts")
    public List<AccountDTO> getAllAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUser(user).stream()
                .map(accountService::toDTO)  // ✅ Usa el método toDTO actualizado
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> findById(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findById(id)
                .filter(account -> account.getUser().getId().equals(user.getId()))
                .map(accountService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
