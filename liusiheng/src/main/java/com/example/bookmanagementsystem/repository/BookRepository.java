package com.example.bookmanagementsystem.repository;

import com.example.bookmanagementsystem.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);

    // 扩展功能：按分类查询
    List<Book> findByCategoryId(Long categoryId);

    // 按分类统计图书数量
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    // 修复：基于实际存在的字段进行查询
    @Query("SELECT b FROM Book b ORDER BY b.borrowCount DESC")
    List<Book> findMostBorrowedBooks(Pageable pageable);
}
