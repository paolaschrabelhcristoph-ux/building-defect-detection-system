package com.example.bookmanagementsystem.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer totalCopies = 0;

    @Column(nullable = false)
    private Integer availableCopies = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 在Book实体类中添加borrowCount字段
    @Column(name = "borrow_count")
    private Integer borrowCount = 0;

    // 添加getter和setter方法
    public Integer getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(Integer borrowCount) {
        this.borrowCount = borrowCount;
    }
    // 确保Book实体类中已包含分类字段
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Constructors
    public Book() {
    }

    public Book(String title, String author, String isbn, String description, Integer totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.totalCopies = totalCopies != null ? totalCopies : 0;
        this.availableCopies = this.totalCopies;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    // 添加getter和setter方法
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
        // 当总数改变时，相应调整可用副本数（增加空值检查）
        if (this.availableCopies != null && this.totalCopies != null && this.availableCopies > this.totalCopies) {
            this.availableCopies = this.totalCopies;
        }
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
