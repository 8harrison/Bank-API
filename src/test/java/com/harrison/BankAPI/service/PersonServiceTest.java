package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class PersonServiceTest {

  @Autowired
  private PersonService personService;

  Person person;

  Person saved;

  @BeforeEach
  public void setup() {
    person = mockPerson();
    System.out.println(objectToJson(personService.getAll()));
    saved = personService.register(person);

  }

  @Test
  public void testRegister() {
    String response = objectToJson(saved);
    person.setId(saved.getId());
    person.setPassword(saved.getPassword());
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
