package com.harrison.BankAPI.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Autowired
  private AccountService accountService;

  @Autowired
  private BranchService branchService;

  private final Branch branch = branchService.createBranch(BranchFixtures.branch_1);

  private final Account saved1 = accountService.createAccount(AccountFixtures.account1);

  @Test
  public void testCreateAccount() {
    MockGen expected = AccountFixtures.account1;
    expected.put("id", saved1.getId());
    expected.put("code", saved1.getCode());

    MockGen response = new MockGen(saved1);

    assertEquals(expected, response);
  }

  @Test
  public void testGetById() {
    Account founded = accountService.getById(saved1.getId());

    MockGen expected = new MockGen(saved1);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testByGetIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getById(100L));
  }

  @Test
  public void testGetByCode() {
    Account founded = accountService.getByCode(saved1.getCode());

    MockGen expected = new MockGen(saved1);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByCodeNotFound() {
    assertThrows(CodeNotFoundException.class, () ->
        accountService.getByCode("0000-00000"));
  }

  @Test
  public void testUpdateAccount() {
    Account founded = accountService.getById(saved1.getId());
    founded.setName("Moacir Antunes");
    founded.setPassword("abcde");
    founded.setEmail("moacir.antunes@gmail.com");
    founded.setUsername("moacirantunes");
    Account updated = accountService.updateAccount(saved1.getId(), founded);

    MockGen expected = new MockGen(founded);
    MockGen response = new MockGen(updated);

    assertEquals(expected, response);
  }

  @Test
  public void testDeleteAccount() {
    Account saved = accountService.createAccount(AccountFixtures.account3);

    String message = accountService.deleteAccount(saved.getId());

    assertEquals("Conta excluída com sucesso!", message);
    assertThrows(IdNotFoundException.class, () ->
        accountService.getById(saved.getId()));
  }

  @Test
  public void testSetAdress() {
    MockGen address = AddressFixtures.client_address1;
    Account saved = accountService.setAddress(1L, address);

    saved1.setAddress(address);
    address.setId(saved.getId());

    MockGen expected = new MockGen(saved1);
    MockGen response = new MockGen(saved);
    assertEquals(expected, response);
  }

  @Test
  public void testSetAddressNotFoundAccountId() {
    MockGen address = AddressFixtures.client_address1;

    assertThrows(IdNotFoundException.class, () ->
        accountService.createAddress(100L, address));
  }

}
