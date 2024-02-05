package com.harrison.BankAPI;

import com.harrison.BankAPI.service.PersonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class PersonController {

  private PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }
}
