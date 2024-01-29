package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.getValidateToken;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.TestHelpers;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@ActiveProfiles("test")
public class AccountControllerTest {

  TestHelpers aux = new TestHelpers();

  @Test
  public void testCreateAccount() throws Exception {
    MockGen account = AccountFixtures.account1;
    MockGen savedAccount = aux.performCreation(account);

    assertNotNull(savedAccount.get("id"), "A resposta deve incluir o id da conta criada!");
    assertNotNull(savedAccount.get("branchId"), "A resposta deve incluir o id da branch!");
    MockGen expectedAccount = new MockGen(account);
    expectedAccount.put("id", savedAccount.get("id"));

    assertEquals(expectedAccount, savedAccount);

  }

  @Test
  public void testgetAll() throws Exception {
    Set<MockGen> accounts = Set.of(
        AccountFixtures.account1,
        AccountFixtures.account2,
        AccountFixtures.account3
    );

    Set<MockGen> expectedAccounts = new HashSet<>();
    for (MockGen account : accounts) {
      MockGen savedAccount = aux.performCreation(account);
      expectedAccounts.add(savedAccount);
    }
    String expected = objectToJson(expectedAccounts);
    String returnedAccounts = aux.performfind("/accounts");

    assertEquals(expected, returnedAccounts);

  }

  @Test
  public void testGetById() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);

    String expected = objectToJson(account);
    String returnedAccount = aux.performfind("/accounts/1");

    assertEquals(expected, returnedAccount);

  }

  @Test
  public void testGetByIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = get("/accounts/100");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testGetByCode() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);

    String expected = objectToJson(account);
    String returnedAccount = aux.performfind("/accounts?code=" + account.get("code"));

    assertEquals(expected, returnedAccount);

  }

  @Test
  public void testGetByCodeNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = get("/accounts?code=0000-00000");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testUpdateAccount() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = put("/accounts/" + account.get("id"));

    builder = builder.header("Authorization", "Bearer " + getValidateToken());
    account.put("email", "moacir.antunes@gmail.com");
    MockGen expected = new MockGen(account);
    MockGen returnedAccount = aux.performUpdate(builder, account);

    assertEquals(expected, returnedAccount);

  }

  @Test
  public void testUpdateAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = put("/accounts/100");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testDeleteAccount() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = delete("/accounts/" + account.get("id"));
    builder = builder.header("Authorization", "Bearer " + getValidateToken());
    String message = "Conta excluída com sucesso!";
    aux.performDelete(builder, message);
  }

  @Test
  public void testDeleteAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = delete("/accounts/100");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testSetAddress() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockGen address = AddressFixtures.client_address1;
    MockGen created = aux.performCreation(address, "/accounts/1/address");
    account.put("address", address);
    assertEquals(account, created);
  }

  @Test
  public void testCreateAddressNotFoundAccountId() throws Exception {
    MockHttpServletRequestBuilder builder = post("/accounts/100/address");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }
}
