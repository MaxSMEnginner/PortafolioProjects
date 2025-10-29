package com.maxacm.lr.service.categorys;
import com.maxacm.lr.dto.accounts.UpdateAccount;
import com.maxacm.lr.dto.categorys.NewCategory;
import com.maxacm.lr.dto.categorys.UpdateCategory;
import com.maxacm.lr.entity.Account;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.entity.Category;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.repository.categorys.CategoryRepository;
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


    public Category updateCategory(Long id, UpdateCategory dto){
        return categoryRepository.findById(id).map(category -> {

            dto.name().ifPresent(category::setName);
            return categoryRepository.save(category);

        }).orElseThrow(() -> new RuntimeException("Not Found Category"));

    }


}
