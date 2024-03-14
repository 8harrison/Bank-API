package com.harrison.BankAPI.models.repository;

import com.harrison.BankAPI.models.entity.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByCode(String code);

    @Query(value = "SELECT * FROM transactions " +
            "WHERE account_id = :id " +
            "AND created_date BETWEEN :start AND :end",
            nativeQuery = true)
    List<Transaction> getTransactionsByPeriod(Long id, LocalDate start, LocalDate end);
}
