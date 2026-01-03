package com.example.bookmanagementsystem.controller;

import com.example.bookmanagementsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class MainController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "index";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "index";
    }
}
