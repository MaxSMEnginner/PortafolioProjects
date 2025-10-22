package com.maxacm.lr.repository.accounts;
import com.maxacm.lr.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maxacm.lr.entity.User;
import java.util.List;
import java.util.Optional;

import com.maxacm.lr.Enum.TypeAccounts.TypeAccount;

public interface AccountRepository extends JpaRepository<Account, Long>{
    //List<Account> findByUserId(Long id); es lo mismo que lo de findByUser
    List<Account> findByUser(User user);
    Optional<Account> findByNameAndTypeAndUser(String name, TypeAccount type,User user);

    boolean existsByNameAndTypeAndUser(String Name, TypeAccount type, User user);
    List<Account> findByName(String name);
    List<Account> findByType(String type);
}
