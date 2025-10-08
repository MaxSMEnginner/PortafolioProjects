package com.maxacm.lr.repository;

import com.maxacm.lr.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maxacm.lr.entity.User;

import java.util.List;



public interface CategoryRepository extends JpaRepository<Category,Long>{
    List<Category> findByUser(User user);
    List<Category> findByName(String Name);
    List<Category> findByType(String type);
}
