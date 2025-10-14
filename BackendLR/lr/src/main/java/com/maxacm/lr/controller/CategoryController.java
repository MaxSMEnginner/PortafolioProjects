package com.maxacm.lr.controller;

import com.maxacm.lr.dto.NewCategory;
import com.maxacm.lr.repository.CategoryRepository;
import com.maxacm.lr.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody NewCategory newCategory,
                                        @AuthenticationPrincipal UserDetails userDetails){
        categoryService.newcategory(newCategory, userDetails);
        return ResponseEntity.ok("Category created successfully");

    }
}
