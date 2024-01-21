package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.Mock.personMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PersonServiceTest {

  @Autowired
  private PersonService personService;

  private final Person saved = personService.register(personMock());

  private final Person person = personMock();

  @Test
  public void testRegister() {
    assertEquals(saved.getName(), person.getName());
    assertEquals(saved.getCpf(), person.getCpf());
    assertEquals(saved.getEmail(), person.getEmail());
    assertEquals(saved.getUsername(), person.getUsername());
    assertEquals(saved.getRole(), person.getRole());
  }

  @Test
  public void testGetByCpf() {
    Person founded = personService.getByCpf(saved.getCpf());

    assertEquals(saved.getName(), founded.getName());
    assertEquals(saved.getCpf(), founded.getCpf());
    assertEquals(saved.getEmail(), founded.getEmail());
    assertEquals(saved.getUsername(), founded.getUsername());
    assertEquals(saved.getRole(), founded.getRole());
  }

  @Test
  public void testGetByCpfNotFound() {
    assertThrows(CpfNotFoundException.class, () ->
        personService.getByCpf("87654321"));
  }

  @Test
  public void testGetById() {
    Person founded = personService.getById(saved.getId());
    assertEquals(saved.getName(), founded.getName());
    assertEquals(saved.getCpf(), founded.getCpf());
    assertEquals(saved.getEmail(), founded.getEmail());
    assertEquals(saved.getUsername(), founded.getUsername());
    assertEquals(saved.getRole(), founded.getRole());
  }

  @Test
  public void testGetByIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        personService.getById(100L));
  }
}
