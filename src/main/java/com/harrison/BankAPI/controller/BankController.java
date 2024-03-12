package com.harrison.BankAPI.controller;

import com.harrison.BankAPI.controller.dto.UpdateBankDto;
import com.harrison.BankAPI.models.entity.Bank;
import com.harrison.BankAPI.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @Secured({"MANAGER"})
    public ResponseEntity<Bank> create(@RequestBody Bank bank) {
        Bank created = bankService.create(bank);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured({"MANAGER"})
    public ResponseEntity<Bank> updateTax(@PathVariable Long id, @RequestBody UpdateBankDto dto) {
        Bank update = bankService.updateTax(id, dto.tax());
        return ResponseEntity.ok(update);
    }
}
