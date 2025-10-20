package com.maxacm.lr.repository;

import com.maxacm.lr.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.Enum.TypeTransaction;
import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category,Long>{
    Boolean existsByNameAndUser(String Name, User user);
    Optional<Category> findByNameAndUser(String name, User user);
    List<Category> findByUser(User user);
    List<Category> findByName(String Name);
    List<Category> findByType(TypeTransaction type);
}
