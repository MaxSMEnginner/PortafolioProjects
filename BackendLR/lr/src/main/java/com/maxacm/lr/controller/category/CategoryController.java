package com.maxacm.lr.controller.category;

import com.maxacm.lr.dto.categorys.CategoryDTO;
import com.maxacm.lr.dto.categorys.UpdateCategory;
import com.maxacm.lr.dto.categorys.NewCategory;
import com.maxacm.lr.entity.Category;
import com.maxacm.lr.entity.User;
import com.maxacm.lr.exception.categorys.CategoryAlreadyExistsException;
import com.maxacm.lr.exception.categorys.CategoryNotFoundException;
import com.maxacm.lr.repository.categorys.CategoryRepository;
import com.maxacm.lr.repository.users.UserRepository;
import com.maxacm.lr.service.categorys.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewCategory newCategory,
                                        @AuthenticationPrincipal UserDetails userDetails){
        categoryService.newcategory(newCategory, userDetails);
        return ResponseEntity.ok("Category created successfully");

    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateCategory dto) {
        try{
            Category updatecategory= categoryService.updateCategory(id, dto);
            return ResponseEntity.ok(categoryService.toDTO(updatecategory));
        }catch(CategoryAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }catch(CategoryNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: "+ e.getMessage());
        }

    }



    @GetMapping("/categorys")
    public List<CategoryDTO> getAllCategorys(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUser(user).stream()
                .map(categoryService::toDTO)  // ✅ Usa el método toDTO actualizado
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findById(id)
                .filter(category -> category.getUser().getId().equals(user.getId()))
                .map(categoryService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
