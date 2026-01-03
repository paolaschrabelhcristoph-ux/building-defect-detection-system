package com.example.bookmanagementsystem.service;

import com.example.bookmanagementsystem.entity.Book;
import com.example.bookmanagementsystem.entity.BorrowRecord;
import com.example.bookmanagementsystem.entity.User;
import com.example.bookmanagementsystem.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowRecordService {
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public List<BorrowRecord> getBorrowRecordsByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }

    public List<BorrowRecord> getBorrowRecordsByBookId(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId);
    }

    public List<BorrowRecord> getActiveBorrowsByUserId(Long userId) {
        return borrowRecordRepository.findActiveBorrowsByUserId(userId);
    }

    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDateTime.now());
    }

    public BorrowRecord borrowBook(Long userId, Long bookId, int days) {
        Optional<User> userOpt = Optional.ofNullable(userService.getUserById(userId));
        Optional<Book> bookOpt = bookService.getBookById(bookId);

        if (!userOpt.isPresent() || !bookOpt.isPresent()) {
            throw new RuntimeException("用户或图书不存在");
        }

        User user = userOpt.get();
        Book book = bookOpt.get();

        if (!bookService.borrowBook(bookId)) {
            throw new RuntimeException("图书不可借阅或库存不足");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(days));
        record.setStatus(BorrowRecord.BorrowStatus.BORROWED);

        return borrowRecordRepository.save(record);
    }

    public BorrowRecord returnBook(Long recordId) {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(recordId);
        if (!recordOpt.isPresent()) {
            throw new RuntimeException("借阅记录不存在");
        }

        BorrowRecord record = recordOpt.get();
        if (record.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("图书已归还或状态异常");
        }

        // 计算罚款
        if (record.getDueDate().isBefore(LocalDateTime.now())) {
            // 简单罚款计算：每逾期一天罚0.5元
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                    record.getDueDate(), LocalDateTime.now());
            record.setFine(java.math.BigDecimal.valueOf(overdueDays * 0.5));
        }

        // 更新图书可用数量
        bookService.returnBook(record.getBook().getId());

        // 更新借阅记录
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);

        return borrowRecordRepository.save(record);
    }

    public Long countActiveBorrowsByUserId(Long userId) {
        return borrowRecordRepository.countActiveBorrowsByUserId(userId);
    }
}
