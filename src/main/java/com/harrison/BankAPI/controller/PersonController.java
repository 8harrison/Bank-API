package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.controller.dto.PersonDto.toDto;

import com.harrison.BankAPI.controller.dto.PersonDto;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.service.PersonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/people")
public class PersonController {

  private final PersonService personService;

  @Autowired
  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonDto> getById(@PathVariable Long id) {
    Person person = personService.getById(id);
    return ResponseEntity.ok(toDto(person));
  }

  @GetMapping
  public ResponseEntity<List<PersonDto>> GetAll() {
    List<Person> people = personService.getAll();
    List<PersonDto> dtos = people.stream()
        .map(PersonDto::toDto).toList();
    return ResponseEntity.ok(dtos);
  }
}
