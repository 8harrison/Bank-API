package com.harrison.BankAPI.models.repository;

import com.harrison.BankAPI.models.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
