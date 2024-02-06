package com.harrison.BankAPI.models.repository;

import com.harrison.BankAPI.models.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {


}
