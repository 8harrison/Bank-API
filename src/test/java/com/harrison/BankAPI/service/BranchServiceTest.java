package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BranchServiceTest {

  @Autowired
  BranchService branchService;

  @Test
  public void test() throws JsonProcessingException {
    testCreateBranch();
    testGetById();
    testGetByIdNotFound();
    testGetAll();
    testUpdate();
    testUpdateNotFound();
    testDelete();
    testDeleteNotFound();
    testSetAddress();
    testSetAddressBranchNotFound();
  }

  private void testCreateBranch() throws JsonProcessingException {
    MockGen branch = BranchFixtures.branch_1;
    Branch created = branchService.create(branch.toBranch());
    branch.put("id", created.getId());
    String response = objectToJson(created);
    String expected = objectToJson(branch);
    assertEquals(expected, response);
  }

  private void testGetById() throws JsonProcessingException {
    Branch branch = branchService.create(BranchFixtures.branch_2.toBranch());
    Branch founded = branchService.getById(branch.getId());

    String expected = objectToJson(branch);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  private void testGetByIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        branchService.getById(100L));
  }

  private void testGetAll() throws JsonProcessingException {
    Branch created = branchService.create(BranchFixtures.branch_3.toBranch());
    MockGen branch1 = BranchFixtures.branch_1;
    MockGen branch2 = BranchFixtures.branch_2;
    MockGen branch3 = MockGen.toMockGen(objectToJson(created));
    branch1.put("id", 1L);
    branch2.put("id", 2L);
    Set<MockGen> expected = Set.of(branch1, branch2, branch3);
    Set<Branch> response = new HashSet<>(branchService.getAll());

    assertEquals(objectToJson(expected), objectToJson(response));
  }

  private void testUpdate() {
    Branch founded = branchService.getById(1L);
    founded.setName("Agência da Praça da Igreja da Matriz");
    Branch updated = branchService.update(1L, founded);

    String expected = objectToJson(founded);
    String response = objectToJson(updated);

    assertEquals(expected, response);
  }

  private void testUpdateNotFound() {
    Branch branch = branchService.getById(1L);
    assertThrows(NotFoundException.class, () ->
        branchService.update(100L, branch));
  }

  private void testDelete() {
    String response = branchService.delete(3L);
    String expected = "Agência excluída com sucesso!";
    assertEquals(expected, response);
    assertThrows(NotFoundException.class, () ->
        branchService.getById(3L));
  }

  private void testDeleteNotFound() {
    assertThrows(NotFoundException.class, () ->
        branchService.delete(100L));
  }

  private void testSetAddress() throws JsonProcessingException {
    MockGen address = AddressFixtures.branch_address1;
    Branch founded = branchService.getById(1L);
    Branch branch = branchService.setAddress(1L, address.toAddress());
    founded.setAddress(address.toAddress());
    String expected = objectToJson(founded);
    String response = objectToJson(branch);

    assertEquals(expected, response);
  }

  private void testSetAddressBranchNotFound() {
    assertThrows(NotFoundException.class, () ->
        branchService.setAddress(100L, AddressFixtures.branch_address1.toAddress()));
  }

}
