package com.harrison.BankAPI;

import com.harrison.BankAPI.models.repository.*;

import com.harrison.BankAPI.service.AccountService;

import com.harrison.BankAPI.service.BranchService;
import com.harrison.BankAPI.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class Config {

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  AddressRepository addressRepository;
  @Autowired
  BankRepository bankRepository;
  @Autowired
  BranchRepository branchRepository;
  @Autowired
  PersonRepository personRepository;
  @Autowired
  TransactionRepository transactionRepository;







  @Autowired
  private WebApplicationContext webApplicationContext;


  @Bean
  public AccountService accountService() {
    return new AccountService(accountRepository, addressRepository, bankRepository,
        branchRepository, personRepository, transactionRepository);
  }

  @Bean
  public BranchService branchService() {
    return new BranchService(branchRepository, personRepository, addressRepository);
  }

  @Bean
  public PersonService personService() {
    return new PersonService(personRepository);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
