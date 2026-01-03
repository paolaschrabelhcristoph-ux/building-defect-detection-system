package com.example.bookmanagementsystem.controller;

import com.example.bookmanagementsystem.dto.BookDto;
import com.example.bookmanagementsystem.entity.Book;
import com.example.bookmanagementsystem.entity.Category;
import com.example.bookmanagementsystem.service.BookService;
import com.example.bookmanagementsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private CategoryService categoryService;

    // 显示图书列表页面
    // 修改listBooks方法，添加分类数据
    @GetMapping
    public String listBooks(@RequestParam(required = false) String keyword, Model model) {
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = bookService.searchBooks(keyword);
        } else {
            books = bookService.getAllBooks();
        }
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "book-list";
    }

    // 添加按类别查询的方法
    @GetMapping("/category/{categoryId}")
    public String listBooksByCategory(@PathVariable Long categoryId, Model model) {
        List<Book> books = bookService.getBooksByCategoryId(categoryId);
        Category category = categoryService.getCategoryById(categoryId);
        model.addAttribute("books", books);
        model.addAttribute("categoryName", category != null ? category.getName() : "未知分类");
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "book-list";
    }


    // 保存图书（新增或更新）
    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDto bookDto, BindingResult result, Model model) {
        // 检查ISBN是否重复
        if (bookService.isIsbnExists(bookDto.getIsbn(), bookDto.getId())) {
            result.rejectValue("isbn", "error.book", "ISBN已存在");
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", bookDto.getId() != null);
            return "book-form";
        }

        // 将BookDto转换为Book实体
        Book book = new Book();
        if (bookDto.getId() != null) {
            // 更新现有图书
            Optional<Book> existingBook = bookService.getBookById(bookDto.getId());
            if (existingBook.isPresent()) {
                book = existingBook.get();
            }
        }

        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setDescription(bookDto.getDescription());
        book.setTotalCopies(bookDto.getTotalCopies());

        // 设置分类
        if (bookDto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(bookDto.getCategoryId());
            if (category != null) {
                book.setCategory(category);
            }
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }


    // 删除图书
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    // 借书
    @PostMapping("/borrow/{id}")
    @ResponseBody
    public ResponseEntity<String> borrowBook(@PathVariable Long id) {
        boolean success = bookService.borrowBook(id);
        if (success) {
            return ResponseEntity.ok("借书成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("借书失败：图书不可用或库存不足");
        }
    }

    // 还书
    @PostMapping("/return/{id}")
    @ResponseBody
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        boolean success = bookService.returnBook(id);
        if (success) {
            return ResponseEntity.ok("还书成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("还书失败：图书已全部归还");
        }
    }

    // 在BookController中添加分类字段的getter和setter
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new BookDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "book-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            // 将Book转换为BookDto
            BookDto bookDto = new BookDto();
            bookDto.setId(book.get().getId());
            bookDto.setTitle(book.get().getTitle());
            bookDto.setAuthor(book.get().getAuthor());
            bookDto.setIsbn(book.get().getIsbn());
            bookDto.setDescription(book.get().getDescription());
            bookDto.setTotalCopies(book.get().getTotalCopies());

            model.addAttribute("book", bookDto);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", true);
            return "book-form";
        } else {
            return "redirect:/books";
        }
    }
}
