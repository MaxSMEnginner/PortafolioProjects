package com.maxacm.lr.repository;
import com.maxacm.lr.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maxacm.lr.entity.User;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>{
    //List<Account> findByUserId(Long id); es lo mismo que lo de findByUser
    List<Account> findByUser(User user);
    List<Account> findByName(String name);
    List<Account> findByType(String type);
}
