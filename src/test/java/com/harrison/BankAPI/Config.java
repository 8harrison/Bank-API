package com.harrison.BankAPI;

import com.harrison.BankAPI.models.repository.AccountRepository;
import com.harrison.BankAPI.models.repository.AddressRepository;
import com.harrison.BankAPI.models.repository.BranchRepository;
import com.harrison.BankAPI.models.repository.PersonRepository;
import com.harrison.BankAPI.models.repository.TransactionRepository;

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
  TransactionRepository transactionRepository;

  @Autowired
  BranchRepository branchRepository;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  AddressRepository addressRepository;

  @Autowired
  private WebApplicationContext webApplicationContext;


  @Bean
  public AccountService accountService() {
    return new AccountService(accountRepository, transactionRepository, branchRepository,
        personRepository, addressRepository);
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
