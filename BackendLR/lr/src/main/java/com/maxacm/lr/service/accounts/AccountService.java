package com.maxacm.lr.service.accounts;

import com.maxacm.lr.dto.accounts.AccountDTO;
import com.maxacm.lr.dto.accounts.UpdateAccount;
import com.maxacm.lr.dto.users.UserDTO;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.repository.accounts.AccountRepository;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.entity.Account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.maxacm.lr.dto.accounts.NewAccount;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    // ✅ Método helper para convertir User a UserDTO
    public AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .user(account.getUser())
                .currentBalance(account.getCurrentBalance())
                .build();
    }


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
                .currentBalance(newAccount.getCurrentBalance())
                .type(newAccount.getType())
                .user(user)
                .build();
        return accountRepository.save(account);
    }



    public Account updateAccount(Long id, UpdateAccount dto){
        return accountRepository.findById(id).map(account -> {

            dto.name().ifPresent(account::setName);
            dto.type().ifPresent(account::setType);
            return accountRepository.save(account);

        }).orElseThrow(() -> new RuntimeException("Not Found Account"));

    }


}







