package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.repository.BranchRepository;
import com.harrison.BankAPI.utils.Address;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

  private final BranchRepository branchRepository;

  @Autowired
  public BranchService(BranchRepository branchRepository) {
    this.branchRepository = branchRepository;
  }

  public Branch create(Branch branch) {
    Branch created = branchRepository.save(branch);
    created.setCode(generateCode(created.getId()));
    return created;
  }

  public Branch getById(Long id) {
    Optional<Branch> branch = branchRepository.findById(id);

    if (branch.isEmpty()) {
      throw new NotFoundException("Agência não encontrada!");
    }
    return branch.get();
  }

  public Branch getByCode(String code) {
    Optional<Branch> branch = branchRepository.findByCode(code);

    if (branch.isEmpty()) {
      throw new NotFoundException("Agência não encontrada!");
    }
    return branch.get();
  }

  public List<Branch> getAll() {
    return branchRepository.findAll();
  }

  public Branch update(Long id, Branch branch) {
    Optional<Branch> founded = branchRepository.findById(id);

    if (founded.isEmpty()) {
      throw new NotFoundException("Agência não encontrada!");
    }
    branch.setId(id);

    return branchRepository.save(branch);
  }

  public String delete(Long id) {
    Optional<Branch> branch = branchRepository.findById(id);

    if (branch.isEmpty()) {
      throw new NotFoundException("Agência não encontrada!");
    }
    branchRepository.deleteById(id);
    return "Agência excluída com sucesso!";
  }

  public Branch setAddress(Long id, Address address) {
    Optional<Branch> branch = branchRepository.findById(id);

    if (branch.isEmpty()) {
      throw new NotFoundException("Agência não encontrada!");
    }
    branch.get().setAddress(address);

    return branchRepository.save(branch.get());
  }

  public String generateCode(Long id) {
    String code = "" + id;
    while (code.length() < 4) {
      code = "0" + code;
    }
    return code;
  }
}
