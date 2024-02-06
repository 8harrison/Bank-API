package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.ConflictUsernameException;
import com.harrison.BankAPI.exception.InvalidCpfException;
import com.harrison.BankAPI.exception.InvalidEmailException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.repository.PersonRepository;
import java.util.List;
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

  private Person pessoaLogada;

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
    verifyCpf(person.getCpf());
    verifyEmail(person.getEmail());
    isUserNameConflict(person.getUsername());
    return personRepository.save(person);
  }

  public Person getById(Long id) {
    Optional<Person> person = personRepository.findById(id);

    if (person.isEmpty()) {
      throw new NotFoundException("Pessoa não encontrada!");
    }

    return person.get();
  }

  public void pessoaLogada(Person person) {
    this.pessoaLogada = person;
  }

  public String pessoaLogada() {
    return pessoaLogada.getUsername();
  }

  public List<Person> getAll() {
    return personRepository.findAll();
  }

  private void verifyCpf(String cpf) {
    if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
      throw new InvalidCpfException("Por favor, digite um CPF válido!");
    }
  }

  private void verifyEmail(String email) {
    if (!email.matches("[^1-9][a-zA-Z.0-9]+@[a-zA-Z]+.[a-zA-Z]+$")) {
     throw new InvalidEmailException("Por favor, digite um email válido!");
    }
  }

  private void isUserNameConflict(String username) {
    List<Person> people = personRepository.findAll();
    people.forEach(person -> {
      if (person.getUsername().equals(username)) {
        throw new ConflictUsernameException("Já existe um usuário com este Username!");
      }
    });
  }
}
