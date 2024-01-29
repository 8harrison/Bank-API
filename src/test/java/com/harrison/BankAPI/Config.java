package com.harrison.BankAPI;




import com.harrison.BankAPI.models.repository.AccountRepository;
import com.harrison.BankAPI.models.repository.BranchRepository;
import com.harrison.BankAPI.models.repository.PersonRepository;
import com.harrison.BankAPI.models.repository.TransactionRepository;

import com.harrison.BankAPI.service.AccountService;

import com.harrison.BankAPI.service.BranchService;
import com.harrison.BankAPI.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  BranchRepository branchRepository;

  @Autowired
  PersonRepository personRepository;

  @Bean
  public AccountService accountService() {
    return new AccountService(accountRepository, transactionRepository, branchRepository, personRepository);
  }

  @Bean
  public BranchService branchService() {
    return new BranchService(branchRepository, personRepository);
  }

  @Bean
  public PersonService personService() {
    return new PersonService(personRepository);
  }

}
