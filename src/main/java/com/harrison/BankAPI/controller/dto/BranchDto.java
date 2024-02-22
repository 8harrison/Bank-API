package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;

import java.time.LocalDate;
import java.util.List;

public record BranchDto(Long id, String name, String code, Address address, LocalDate createdDate, LocalDate lastModifiedDate) {

  public static BranchDto toDto(Branch branch) {
    return new BranchDto(
        branch.getId(),
        branch.getName(),
        branch.getCode(),
        branch.getAddress(),
        branch.getCreatedDate(),
        branch.getLastModifiedDate()
    );
  }
}
