package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.MockFactory.mockAddress;
import static com.harrison.BankAPI.mocks.MockFactory.mockBranch_1;
import static com.harrison.BankAPI.mocks.MockFactory.mockBranch_2;
import static com.harrison.BankAPI.mocks.MockFactory.mockBranch_3;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BranchServiceTest {

  @Autowired
  BranchService branchService;

  Branch branch;

  Branch saved;

  @BeforeEach
  public void setup() {
    branch = mockBranch_1();
    saved = branchService.create(branch);
  }

  @Test
  public void test() throws JsonProcessingException, NoSuchFieldException, IllegalAccessException {
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


  private void testCreateBranch() {
    branch.setId(saved.getId());
    branch.setCode(saved.getCode());
    String response = objectToJson(saved);
    String expected = objectToJson(branch);
    assertEquals(expected, response);
  }




  private void testGetById() {
    Branch founded = branchService.getById(saved.getId());
    MockGen expected = MockGen.toMockGen(saved);
    MockGen response = MockGen.toMockGen(founded);

    assertEquals(expected, response);
  }

  private void testGetByIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        branchService.getById(100L));
  }

  private void testGetAll() {
    Branch branch2 = branchService.create(mockBranch_2());
    Branch branch3 = branchService.create(mockBranch_3());
    List<Branch> branches = List.of(saved, branch2, branch3);
    List<Branch> response = branchService.getAll();

    assertEquals(objectToJson(branches), objectToJson(response));
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

  private void testSetAddress() {
    Address address = mockAddress();
    Branch founded = branchService.getById(1L);
    Branch branch = branchService.setAddress(1L, address);
    founded.setAddress(address);
    String expected = objectToJson(founded);
    String response = objectToJson(branch);

    assertEquals(expected, response);
  }

  private void testSetAddressBranchNotFound() {
    assertThrows(NotFoundException.class, () ->
        branchService.setAddress(100L, AddressFixtures.branch_address1.toAddress()));
  }

}
