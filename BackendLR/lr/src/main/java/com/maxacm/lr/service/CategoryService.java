package com.maxacm.lr.service;
import com.maxacm.lr.dto.NewCategory;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.entity.Category;
import com.maxacm.lr.repository.UserRepository;
import com.maxacm.lr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Category newcategory(NewCategory newCategory, UserDetails userDetails){
        //JWT con la accede con Login
        String username= userDetails.getUsername();
        //ENCONTRAMOS AL USUARIO
        User user= userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));

        boolean exist = categoryRepository.existsByNameAndUser(
                newCategory.getName(),
                user
        );


        if (exist){
            throw new RuntimeException("The category already exists for this user");
        }

        Category category = Category.builder()
                .name(newCategory.getName())
                .type(newCategory.getType())
                .user(user)
                .build();
        return categoryRepository.save(category);



    }


}
