package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.controller.dto.BranchDto.toDto;

import com.harrison.BankAPI.controller.dto.BranchDto;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.service.BranchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/branches")
public class BranchController {

  private BranchService branchService;

  @Autowired
  public BranchController(BranchService branchService) {
    this.branchService = branchService;
  }

  @PostMapping
  @Secured({"MANAGER"})
  public ResponseEntity<BranchDto> create(@RequestBody Branch branch) {
    Branch created = branchService.create(branch);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
  }

  @GetMapping("/{id}")
  @Secured({"MANAGER"})
  public ResponseEntity<BranchDto> getById(@PathVariable Long id) {
    Branch founded = branchService.getById(id);
    return ResponseEntity.ok(toDto(founded));
  }

  @GetMapping
  @Secured({"MANAGER"})
  public ResponseEntity<List<BranchDto>> getAll() {
    List<Branch> branches = branchService.getAll();
    List<BranchDto> list = branches.stream()
        .map(BranchDto::toDto).toList();
    return ResponseEntity.ok(list);
  }

  @PutMapping("/{id}")
  @Secured({"MANAGER"})
  public ResponseEntity<BranchDto> update(@PathVariable Long id, @RequestBody Branch branch) {
    Branch updated = branchService.update(id, branch);
    return ResponseEntity.ok(toDto(updated));
  }

  @DeleteMapping("/{id}")
  @Secured({"MANAGER"})
  public ResponseEntity<String> delete(@PathVariable Long id) {
    String message = branchService.delete(id);
    return ResponseEntity.ok(message);
  }

  @PutMapping("/{id}/address")
  @Secured({"MANAGER"})
  public ResponseEntity<BranchDto> setAddress(@PathVariable Long id, @RequestBody Address address) {
    Branch created = branchService.setAddress(id, address);
    return ResponseEntity.ok(toDto(created));
  }

}
