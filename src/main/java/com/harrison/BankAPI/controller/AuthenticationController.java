package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.controller.dto.PersonDto.toDto;

import com.harrison.BankAPI.controller.dto.AuthenticationDto;
import com.harrison.BankAPI.controller.dto.PersonDto;
import com.harrison.BankAPI.exception.ConflictUsernameException;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.service.PersonService;
import com.harrison.BankAPI.service.TokenService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  private final PersonService personService;

  private final TokenService tokenService;

  @Autowired
  public AuthenticationController(AuthenticationManager authenticationManager,
      PersonService personService, TokenService tokenService) {
    this.authenticationManager = authenticationManager;
    this.personService = personService;
    this.tokenService = tokenService;
  }

  @PostMapping("/register")
  public ResponseEntity<PersonDto> register(@RequestBody Person person) {
    UserDetails userDetails = personService.loadUserByUsername(person.getUsername());
    Person saved = personService.register(person);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
  }

  @GetMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationDto dto) {
    UsernamePasswordAuthenticationToken usernamePassword =
        new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
    Authentication authentication = authenticationManager.authenticate(usernamePassword);
    Person person = (Person) authentication.getPrincipal();
    String token = tokenService.generateToken(person);
    personService.pessoaLogada(person);
    Map<String, String> response = Map.of("token", token);
    return ResponseEntity.ok(response);
  }
}
