package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import com.harrison.BankAPI.utils.TestHelpers;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BranchControllerTest {

  TestHelpers aux = new TestHelpers();

  @Test
  void testCreate() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = aux.performCreation(branch, "/branches");
    branch.put("id", saved.get("id"));

    assertEquals(branch, saved);
  }

  @Test
  void testGetById() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = aux.performCreation(branch, "/branches");
    String founded = aux.performfind("/branches/1");
    String expect = objectToJson(saved);

    assertEquals(expect, founded);
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    aux.performNotFound(get("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testGetAll() throws Exception {
    List<MockGen> branches = List.of(BranchFixtures.branch_1, BranchFixtures.branch_2, BranchFixtures.branch_3);
    List<MockGen> saved = new ArrayList<>();
    for (MockGen branch : branches) {
      saved.add(aux.performCreation(branch, "/branches"));
    }
    String response = aux.performfind("/branches");
    String expect = objectToJson(saved);

    assertEquals(expect, response);
  }

  @Test
  void testUpdate() throws Exception {
    MockGen branch = BranchFixtures.branch_1;
    MockGen saved = aux.performCreation(branch, "/branches");
    saved.put("name", "Agência da Igreja da Matriz");
    MockGen update = aux.performUpdate(put("/branches/1"), saved);

    assertEquals(saved, update);
  }

  @Test
  void testUpdateNotFound() throws Exception {
    aux.performNotFound(put("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testDelete() throws Exception {
    aux.performCreation(BranchFixtures.branch_1, "/branches");
    aux.performDelete(delete("/branches/1"), "Agência excluída com sucesso!");
  }

  @Test
  void testDeleteNotFound() throws Exception {
    aux.performNotFound(delete("/branches/100"), "Agência não encontrada!");
  }

  @Test
  void testSetAddress() throws Exception {
    MockGen expect = aux.performCreation(BranchFixtures.branch_1, "/branches");
    MockGen response = aux.performUpdate(put("/branches/1/address"), AddressFixtures.branch_address1);
    expect.put("address", AddressFixtures.branch_address1);

    assertEquals(expect, response);
  }

  @Test
  void testSetAddressNotFound() throws Exception {
    aux.performNotFound(put("/branches/100/address"), "Agência não encontrada!");
  }
}
