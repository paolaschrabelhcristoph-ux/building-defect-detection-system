package com.example.bookmanagementsystem.controller;

import com.example.bookmanagementsystem.entity.User;
import com.example.bookmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user-list";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);  // 直接接收User对象
        if (user != null) {
            model.addAttribute("user", user);
            return "user-form";
        } else {
            return "redirect:/users";
        }
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result, Model model) {
        // 检查用户名是否已存在
        if (userService.isUsernameExists(user.getUsername(), user.getId())) {
            result.rejectValue("username", "error.user", "用户名已存在");
        }

        if (result.hasErrors()) {
            return "user-form";
        }
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
