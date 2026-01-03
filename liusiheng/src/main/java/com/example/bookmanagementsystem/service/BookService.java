package com.example.bookmanagementsystem.service;

import com.example.bookmanagementsystem.entity.Book;
import com.example.bookmanagementsystem.entity.Category;
import com.example.bookmanagementsystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryService categoryService;
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return bookRepository.searchBooks(keyword);
        } else {
            return bookRepository.findAll();
        }
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        if (book.getId() == null) {
            // 新增时设置初始值
            if (book.getAvailableCopies() == null) {
                book.setAvailableCopies(book.getTotalCopies());
            }
        } else {
            // 更新时保持可用副本数的逻辑
            Book existingBook = bookRepository.findById(book.getId()).orElse(null);
            if (existingBook != null) {
                // 保持现有的可用副本数，除非总数减少导致可用数超过总数
                if (book.getAvailableCopies() == null) {
                    book.setAvailableCopies(existingBook.getAvailableCopies());
                }
                int newAvailable = Math.min(book.getTotalCopies(), book.getAvailableCopies());
                book.setAvailableCopies(newAvailable);
            }
        }
        return bookRepository.save(book);
    }


    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public boolean isIsbnExists(String isbn, Long bookId) {
        boolean exists = bookRepository.existsByIsbn(isbn);
        if (bookId != null) {
            // 如果是更新操作，检查ISBN是否属于当前书籍
            Optional<Book> currentBook = bookRepository.findById(bookId);
            if (currentBook.isPresent() && currentBook.get().getIsbn().equals(isbn)) {
                exists = false; // ISBN属于当前书籍，不算重复
            }
        }
        return exists;
    }

    public boolean borrowBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }
    // 在BookService中添加以下方法
    // 在BookService中添加完整的方法
    public List<Book> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // 添加按分类名称查询的方法
    public List<Book> getBooksByCategoryName(String categoryName) {
        // 从所有分类中找到匹配的分类
        List<Category> categories = categoryService.getAllCategories().stream()
                .filter(category -> category.getName().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());

        if (!categories.isEmpty()) {
            return getBooksByCategoryId(categories.get(0).getId());
        }
        return new ArrayList<>();
    }


}
