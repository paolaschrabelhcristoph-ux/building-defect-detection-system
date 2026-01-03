package com.example.bookmanagementsystem.repository;

import com.example.bookmanagementsystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    
    List<Category> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId")
    Long countBooksByCategoryId(@Param("categoryId") Long categoryId);
}
