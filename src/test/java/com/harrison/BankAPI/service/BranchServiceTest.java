package com.harrison.BankAPI.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.mocks.MockGen;
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
  public void test() {
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
    MockGen expected = BranchFixtures.branch_1;
    Branch branch = branchService.create(expected);
    expected.put("id", branch.getId());
    MockGen response = new MockGen(branch);

    assertEquals(expected, response);
  }

  private void testGetById() {
    Branch branch = branchService.create(BranchFixtures.branch_2);
    Branch founded = branchService.getById(branch.getId());

    MockGen expected = new MockGen(branch);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  private void testGetByIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        branchService.getById(100L));
  }

  private void testGetAll() {
    Branch created = branchService.create(BranchFixtures.branch_3);
    MockGen branch1 = BranchFixtures.branch_1;
    MockGen branch2 = BranchFixtures.branch_2;
    MockGen branch3 = new MockGen(created);
    branch1.put("id", 1L);
    branch2.put("id", 2L);
    Set<MockGen> expected = Set.of(branch1, branch2, branch3);
    Set<MockGen> response = new HashSet<>();
    for (Branch branch : branchService.getAll()) {
      response.add(new MockGen(branch));
    }

    assertEquals(expected, response);
  }

  private void testUpdate() {
    Branch founded = branchService.getById(1L);
    founded.setName("Agência da Praça da Igreja da Matriz");
    Branch updated = branchService.update(1L, founded);

    MockGen expected = new MockGen(founded);
    MockGen response = new MockGen(updated);

    assertEquals(expected, response);
  }

  private void testUpdateNotFound() {
    Branch branch = branchService.getById(1L);
    assertThrows(IdNotFoundException.class, () ->
        branchService.update(100L, branch));
  }

  private void testDelete() {
    String response = branchService.delete(3L);
    String expected = "Agência excluída com sucesso!";
    assertEquals(expected, response);
    assertThrows(IdNotFoundException.class, () ->
        branchService.getById(3L));
  }

  private void testDeleteNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        branchService.delete(100L));
  }

  private void testSetAddress() {
    MockGen address = AddressFixtures.branch_address1;
    Branch founded = branchService.getById(1L);
    Branch branch = branchService.setAddress(1L, address);
    founded.setAddress(address);
    MockGen expected = new MockGen(founded);
    MockGen response = new MockGen(branch);

    assertEquals(expected, response);
  }

  private void testSetAddressBranchNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        branchService.saveAddress(100L, AddressFixtures.branch_address1));
  }

}
