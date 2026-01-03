package com.example.bookmanagementsystem.controller;

import com.example.bookmanagementsystem.entity.Category;
import com.example.bookmanagementsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category-list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);  // 直接接收Category对象
        if (category != null) {
            model.addAttribute("category", category);
            return "category-form";
        } else {
            return "redirect:/categories";
        }
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "category-form";
        }
        categoryService.saveCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
