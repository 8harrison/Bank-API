package com.harrison.BankAPI.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.PersonFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PersonServiceTest {

  @Autowired
  private PersonService personService;

  private final MockGen person = PersonFixtures.person_client;

  private final Person saved = personService.register(person);

  @Test
  public void testRegister() {
    person.put("id", saved.getId());
    MockGen response = new MockGen(saved);

    assertEquals(person, response);
  }

  @Test
  public void testGetByCpf() {
    Person founded = personService.getByCpf(saved.getCpf());

    MockGen expected = new MockGen(saved);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByCpfNotFound() {
    assertThrows(CpfNotFoundException.class, () ->
        personService.getByCpf("87654321"));
  }

  @Test
  public void testGetById() {
    Person founded = personService.getById(saved.getId());

    MockGen expected = new MockGen(saved);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        personService.getById(100L));
  }
}
