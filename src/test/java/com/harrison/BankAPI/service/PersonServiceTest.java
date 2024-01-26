package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Person;
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

  private final Person saved = personService.register(person.toPerson());

  public PersonServiceTest() throws JsonProcessingException {
  }

  @Test
  public void testRegister() {
    String response = objectToJson(saved);
    person.put("id", saved.getId());
    String expect = objectToJson(person);

    assertEquals(expect, response);
  }

  @Test
  public void testGetByCpf() {
    Person founded = personService.getByCpf(saved.getCpf());

    String expected = objectToJson(saved);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByCpfNotFound() {
    assertThrows(NotFoundException.class, () ->
        personService.getByCpf("87654321"));
  }

  @Test
  public void testGetById() {
    Person founded = personService.getById(saved.getId());

    String expected = objectToJson(saved);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetByIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        personService.getById(100L));
  }
}
