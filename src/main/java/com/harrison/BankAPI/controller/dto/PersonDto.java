package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Person;

public record PersonDto(Long id, String name, String email, String cpf, String username, String role) {

  public static PersonDto toDto(Person person) {
    return new PersonDto(
        person.getId(),
        person.getName(),
        person.getEmail(),
        person.getCpf(),
        person.getUsername(),
        person.getRole()
    );
  }
}
