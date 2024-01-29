package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;
import static com.harrison.BankAPI.mocks.MockGen.toMockGen;
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
import java.util.List;
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    MockGen account = AccountFixtures.account_1_request;
    MockGen savedAccount = aux.performCreation(account);

    assertNotNull(savedAccount.get("id"), "A resposta deve incluir o id da conta criada!");
    assertNotNull(savedAccount.get("accountCode"), "A resposta deve incluir o id da branch!");
    MockGen expectedAccount = AccountFixtures.account_1_response;

    assertEquals(expectedAccount, savedAccount);
  }

  @Test
  public void testgetAll() throws Exception {
    String url = "/auth/register";
    aux.performCreation(toMockGen(mockPerson()), url);
    aux.performCreation(toMockGen(mockPerson_1()), url);
    aux.performCreation(AccountFixtures.account_1_request);
    aux.performCreation(AccountFixtures.account_2_request);

    List<MockGen> expectedAccounts = List.of(
        AccountFixtures.account_1_response,
        AccountFixtures.account_2_response
    );

    String expected = objectToJson(expectedAccounts);
    String returnedAccounts = aux.performfind("/accounts");

    assertEquals(expected, returnedAccounts);
  }

  @Test
  public void testGetById() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockGen response = AccountFixtures.account_1_response;
    String expected = objectToJson(response);
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    MockGen account = aux.performCreation(AccountFixtures.account_1_request);
    MockGen response = AccountFixtures.account_1_response;
    String expected = objectToJson(response);
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    MockGen account = aux.performCreation(AccountFixtures.account_1_request);
    MockHttpServletRequestBuilder builder = put("/accounts/" + account.get("id"));

    builder = builder.header("Authorization", "Bearer " + getValidateToken());
    account.put("email", "moacir.antunes@gmail.com");
    MockGen expected = AccountFixtures.account_1_response;
    expected.put("email", "moacir.antunes@gmail.com");
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    MockGen account = aux.performCreation(AccountFixtures.account_1_request);
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    MockGen account = aux.performCreation(AccountFixtures.account_1_request);
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
