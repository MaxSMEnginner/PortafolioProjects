package com.maxacm.lr.service.transactions;

import com.maxacm.lr.dto.accounts.UpdateAccount;
import com.maxacm.lr.dto.transactions.UpdateTransaction;
import com.maxacm.lr.repository.accounts.AccountRepository;
import com.maxacm.lr.repository.categorys.CategoryRepository;
import com.maxacm.lr.repository.transactions.TransactionRepository;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.entity.Transaction;
import com.maxacm.lr.entity.Category;
import com.maxacm.lr.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.maxacm.lr.dto.transactions.NewTransaction;
import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public Transaction newtransaction(NewTransaction newTransaction, UserDetails userDetails){

        //JWT con la accede con Login
        String username= userDetails.getUsername();
        //ENCONTRAMOS AL USUARIO
        User user= userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Account account = accountRepository.findByNameAndTypeAndUser(
                        newTransaction.getAccountName(),
                        newTransaction.getAccountType(),
                        user
                )
                .orElseThrow(()-> new RuntimeException("Account not found"));
        Category category = categoryRepository.findByNameAndUser(
                newTransaction.getCategoryName(),
                user)
                .orElseThrow(()-> new RuntimeException("Category not found"));

        Transaction transaction= Transaction.builder().
                amount(newTransaction.getAmount()).
                date(LocalDate.now()).
                description(newTransaction.getDescription()).
                account(account).
                category(category).
                user(user).
                build();


        if (TypeTransaction.INCOME.toString().equalsIgnoreCase(category.getType().toString())){
            account.setCurrentBalance(
                    account.getCurrentBalance().add(newTransaction.getAmount())
            );
        }else if(TypeTransaction.EXPENSE.toString().equalsIgnoreCase(category.getType().toString())){
            if (account.getCurrentBalance().compareTo(newTransaction.getAmount()) >= 0){
                account.setCurrentBalance(
                  account.getCurrentBalance().subtract(newTransaction.getAmount())
                );

            }else{
                throw new RuntimeException("Insufficient balance. Current balance: " +
                        account.getCurrentBalance() + ", Required: " + newTransaction.getAmount());
            }
        }
        return transactionRepository.save(transaction);



    }


    public Transaction updateTransaction(Long id, UpdateTransaction dto){
        return transactionRepository.findById(id).map(transaction -> {

            dto.description().ifPresent(transaction::setDescription);
            return transactionRepository.save(transaction);

        }).orElseThrow(() -> new RuntimeException("Not Found Account"));

    }


}
