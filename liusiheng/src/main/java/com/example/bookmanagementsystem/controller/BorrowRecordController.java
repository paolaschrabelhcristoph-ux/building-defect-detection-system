package com.example.bookmanagementsystem.controller;

import com.example.bookmanagementsystem.entity.BorrowRecord;
import com.example.bookmanagementsystem.service.BookService;
import com.example.bookmanagementsystem.service.BorrowRecordService;
import com.example.bookmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/borrow")
public class BorrowRecordController {
    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBorrowRecords(Model model) {
        model.addAttribute("records", borrowRecordService.getAllBorrowRecords());
        return "borrow-list";
    }

    @GetMapping("/add")
    public String showAddBorrowForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        return "borrow-add";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam Long userId,
                             @RequestParam Long bookId,
                             @RequestParam int days,
                             Model model) {
        try {
            BorrowRecord record = borrowRecordService.borrowBook(userId, bookId, days);
            return "redirect:/borrow";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("availableBooks", bookService.getAvailableBooks());
            return "borrow-add";
        }
    }

    @PostMapping("/return/{id}")
    @ResponseBody
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        try {
            borrowRecordService.returnBook(id);
            return ResponseEntity.ok("还书成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("还书失败：" + e.getMessage());
        }
    }
}
