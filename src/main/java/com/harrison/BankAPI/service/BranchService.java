package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.repository.BranchRepository;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.repository.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

  private final BranchRepository branchRepository;

  private final PersonRepository personRepository;

  @Autowired
  public BranchService(BranchRepository branchRepository, PersonRepository personRepository) {
    this.branchRepository = branchRepository;
    this.personRepository = personRepository;
  }

  public Branch create(Branch branch) {
    branch.setCode(generateCode());
    return branchRepository.save(branch);
  }

  private Person verifyManager() {
    Optional<Person> diretor = personRepository.findByRole("MANAGER");
    if (diretor.isEmpty()) {
      throw new NotFoundException("Manager não encontrado!");
    }
    return diretor.get();
  }

  public Branch getById(Long id) {
   return verifyBranch(id);
  }

  public Branch getByCode(String code) {
    Optional<Branch> branch = branchRepository.findByCode(code);

    if (branch.isEmpty()) {
      throw new NotFoundException("Agência de código %s não encontrada!".formatted(code));
    }
    return branch.get();
  }

  public List<Branch> getAll() {
    return branchRepository.findAll();
  }

  public Branch update(Long id, Branch branch) {
    Branch founded = verifyBranch(id);
    branch.setId(id); branch.setCode(founded.getCode());
    return branchRepository.save(branch);
  }

  public String delete(Long id) {
   verifyBranch(id);
    branchRepository.deleteById(id);
    return "Agência excluída com sucesso!";
  }

  public Branch setAddress(Long id, Address address) {
    Branch branch = verifyBranch(id);
    branch.setAddress(address);
    return branchRepository.save(branch);
  }

  private Branch verifyBranch(Long id) {
    Optional<Branch> branch = branchRepository.findById(id);
    if (branch.isEmpty()) {
      throw new NotFoundException("Agência de id %s não encontrada!".formatted(id));
    }
    return branch.get();
  }

  public String generateCode() {
    List<Branch> branches = branchRepository.findAll();
    String code = "" + branches.size() + 1;
    while (code.length() < 4) {
      code = "0" + code;
    }
    return code;
  }
}
