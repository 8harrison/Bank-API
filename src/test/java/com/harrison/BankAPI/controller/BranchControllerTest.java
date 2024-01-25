package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static com.harrison.BankAPI.utils.TestHelpers.performCreation;
import static com.harrison.BankAPI.utils.TestHelpers.performDelete;
import static com.harrison.BankAPI.utils.TestHelpers.performNotFound;
import static com.harrison.BankAPI.utils.TestHelpers.performUpdate;
import static com.harrison.BankAPI.utils.TestHelpers.performfind;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BranchControllerTest {

  MockMvc mockMvc;

  @Test
  void testCreate() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = performCreation(branch, "/branches");
    branch.put("id", saved.get("id"));

    assertEquals(branch, saved);
  }

  @Test
  void testGetById() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = performCreation(branch, "/branches");
    String founded = performfind("/branches/1");
    String expect = objectToJson(saved);

    assertEquals(expect, founded);
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    performNotFound(get("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testGetAll() throws Exception {
    List<MockGen> branches = List.of(BranchFixtures.branch_1, BranchFixtures.branch_2, BranchFixtures.branch_3);
    for (MockGen branch : branches) {
      performCreation(branch, "/branches");
    }
    String response = performfind("/branches");
    String expect = objectToJson(branches);

    assertEquals(expect, response);
  }

  @Test
  void testUpdate() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = performCreation(branch, "/branches");
    saved.put("name", "Agência da Igreja da Matriz");
    MockGen update = performUpdate(put("/branches/1"), saved);

    assertEquals(saved, update);
  }

  @Test
  void testUpdateNotFound() throws Exception {
    performNotFound(put("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testDelete() throws Exception {
    performCreation(BranchFixtures.branch_1, "/branches");
    performDelete(delete("/branches/1"), "Agência excluída com sucesso!");
  }

  @Test
  void testDeleteNotFound() throws Exception {
    performNotFound(delete("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testSetAddress() throws Exception {
    MockGen expect = performCreation(BranchFixtures.branch_1, "/branches");
    MockGen response = performUpdate(put("/branches/1/address"), AddressFixtures.branch_address1);
    expect.put("address", AddressFixtures.branch_address1);

    assertEquals(expect, response);
  }

  @Test
  void testSetAddressNotFound() throws Exception {
    performNotFound(put("/branches/100/address"), "Agência não encontrada!");
  }
}
