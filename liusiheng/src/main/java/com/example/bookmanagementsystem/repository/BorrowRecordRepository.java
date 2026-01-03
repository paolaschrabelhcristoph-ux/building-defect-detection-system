package com.example.bookmanagementsystem.repository;

import com.example.bookmanagementsystem.entity.Book;
import com.example.bookmanagementsystem.entity.BorrowRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    
    List<BorrowRecord> findByBookId(Long bookId);
    
    List<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 'BORROWED'")
    List<BorrowRecord> findActiveBorrowsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < :date AND br.status = 'BORROWED'")
    List<BorrowRecord> findOverdueRecords(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 'BORROWED'")
    Long countActiveBorrowsByUserId(@Param("userId") Long userId);
}
