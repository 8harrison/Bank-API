package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harrison.BankAPI.utils.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
public class Branch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany
  private List<Person> gerentes;

  @ManyToOne
  @JoinColumn(name = "diretor_id")
  private Person diretor;

  private Address address;

  private static long num = 1;

  @Column(unique = true)
  private String code;

  @OneToMany
  @JsonIgnore
  private List<Account> accounts;

  public Branch() {
  }

  public Branch(Long id, String name, List<Person> gerentes, Person diretor, Address address, List<Account> accounts) {
    this.id = id;
    this.name = name;
    this.gerentes = gerentes;
    this.diretor = diretor;
    this.address = address;
    this.accounts = accounts;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Person> getGerentes() {
    return gerentes;
  }

  public void setGerentes(List<Person> gerentes) {
    this.gerentes = gerentes;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Person getDiretor() {
    return diretor;
  }

  public void setDiretor(Person diretor) {
    this.diretor = diretor;
  }

}
