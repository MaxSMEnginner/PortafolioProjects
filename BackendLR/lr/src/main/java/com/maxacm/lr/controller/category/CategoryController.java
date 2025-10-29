package com.maxacm.lr.controller.category;

import com.maxacm.lr.dto.categorys.NewCategory;
import com.maxacm.lr.repository.categorys.CategoryRepository;
import com.maxacm.lr.service.categorys.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

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

}
