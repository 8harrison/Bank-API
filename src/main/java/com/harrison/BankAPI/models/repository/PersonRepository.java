package com.harrison.BankAPI.models.repository;

import com.harrison.BankAPI.models.entity.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  Optional<Person> findByUsername(String username);

  Optional<Person> findByCpf(String cpf);

  Optional<Person> findByRole(String role);
}
