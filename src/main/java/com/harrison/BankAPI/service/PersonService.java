package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.repository.PersonRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements UserDetailsService {

  private final PersonRepository personRepository;

  @Autowired
  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Person> person = personRepository.findByUsername(username);
    return person.orElse(null);
  }

  public Person getByCpf(String cpf) {
    Optional<Person> person = personRepository.findByCpf(cpf);

    if (person.isEmpty()) {
      throw new NotFoundException("CPF não encontrado!");
    }

    return person.get();
  }

  public Person register(Person person) {
    String hashedPassword = new BCryptPasswordEncoder()
        .encode(person.getPassword());
    person.setPassword(hashedPassword);
    return personRepository.save(person);
  }

  public Person getById(Long id) {
    Optional<Person> person = personRepository.findById(id);

    if (person.isEmpty()) {
      throw new NotFoundException("Pessoa não encontrada!");
    }

    return person.get();
  }
}
