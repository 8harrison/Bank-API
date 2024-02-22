package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.MockFactory.mockAccount;
import static com.harrison.BankAPI.mocks.MockFactory.mockAddress;
import static com.harrison.BankAPI.mocks.MockFactory.mockBranch_1;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.utils.AddressFixtures;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class AccountServiceTest {

  @Autowired
  AccountService accountService;
  @Autowired
  BranchService branchService;

  @Autowired
  PersonService personService;

  Account saved;

  Account account;

  Branch branch;

  Person person;

  @BeforeEach
  public void setup() {
    account = mockAccount();
    branch = branchService.create(mockBranch_1());
    person = personService.register(mockPerson());
    saved = savedAccount();
  }
  @Test
  public void testCreateAccount() {
    setParams();
    assertEquals(account, saved);
  }

  private Account savedAccount() {
    account.setPerson(person);
    return accountService.createAccount(account, branch.getCode());
  }

  @Test
  public void testCreateAccountPersonNotFound() {
    person.setId(100L);
    account.setPerson(person);
    assertThrows(NotFoundException.class, () ->
        accountService.createAccount(account, branch.getCode()));
  }

  @Test
  public void testCreateAccountBranchNotFound() {
    branch.setCode("0000");
    assertThrows(NotFoundException.class, () ->
        accountService.createAccount(account, branch.getCode()));
  }

  @Test
  public void testGetAllAccounts() {
    Person savedPerson = personService.register(mockPerson_1());
    Account account1 = mockAccount();
    account1.setPerson(savedPerson);
    Account savedAccount = accountService.createAccount(account1, branch.getCode());
    List<Account> list = List.of(saved, savedAccount);
    List<Account> allAccounts = accountService.getAll();
    String expected = objectToJson(list);
    String response = objectToJson(allAccounts);

    assertEquals(expected, response);
  }

  @Test
  public void testGetById() {
    Account founded = accountService.getById(saved.getId());
    String expected = objectToJson(saved);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testByGetIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getById(100L));
  }

  @Test
  public void testGetByCode() {
    Account founded = accountService.getByCode(saved.getCode());
    String expected = objectToJson(saved);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByCodeNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getByCode("0000-00000"));
  }

  @Test
  public void testUpdateAccount() {
    Account founded = accountService.getById(1L);
    founded.getPerson().setPassword("abcde");
    founded.getPerson().setEmail("moacir.antunes@gmail.com");
    founded.getPerson().setUsername("moacirantunes");
    Account updated = accountService.updateAccount(saved.getId(), founded);

    String expected = objectToJson(founded);
    String response = objectToJson(updated);

    assertEquals(expected, response);
  }

  @Test
  public void testDeleteAccount() {
    String message = accountService.deleteAccount(saved.getId());

    assertEquals("Conta excluída com sucesso!", message);
  }

  @Test
  public void testSetAdress() {
    Address address = mockAddress();
    saved.setAddress(address);
    Account account = accountService.setAddress(saved.getId(), address);
    String expected = objectToJson(saved);
    String response = objectToJson(account);
    assertEquals(expected, response);
  }

  @Test
  public void testSetAddressNotFoundAccountId() {
    MockGen address = AddressFixtures.client_address1;

    assertThrows(NotFoundException.class, () ->
        accountService.setAddress(100L, mockAddress()));
  }

  private void setParams() {
    account.setId(saved.getId());
    account.setCode(saved.getCode());
    account.setCreatedDate(saved.getCreatedDate());
    account.setLastModifiedDate(saved.getLastModifiedDate());
  }
}
