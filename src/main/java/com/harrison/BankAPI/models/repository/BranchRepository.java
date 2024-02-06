package com.harrison.BankAPI.models.repository;

import com.harrison.BankAPI.models.entity.Branch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

  Optional<Branch> findByCode(String code);
}
