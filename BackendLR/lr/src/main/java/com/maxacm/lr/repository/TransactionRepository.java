package com.maxacm.lr.repository;
import com.maxacm.lr.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.entity.Category;
import com.maxacm.lr.entity.Account;

import java.time.LocalDate;
import java.util.List;




public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByUser(User user);
    List<Transaction> findByCategory(Category category);
    List<Transaction> findByAccount(Account account);
    //List<Transaction> findByAccountId(Long id)

    List<Transaction> findByType(String type);
    List<Transaction> findByDate(LocalDate date);



}
