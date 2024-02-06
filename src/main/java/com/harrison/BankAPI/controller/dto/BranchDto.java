package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import java.util.List;

public record BranchDto(Long id, String name, String code, Address address) {

  public static BranchDto toDto(Branch branch) {
    return new BranchDto(
        branch.getId(),
        branch.getName(),
        branch.getCode(),
        branch.getAddress()
    );
  }
}
