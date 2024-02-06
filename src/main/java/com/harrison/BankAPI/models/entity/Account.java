package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "person_id")
  @JsonIgnore
  private Person person;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "branch_id")
  private Branch branch;

  private String name;

  private Double saldo;

  @Column(unique = true)
  private String code;

  @OneToMany(mappedBy = "titular", fetch = FetchType.EAGER)
  @JsonIgnore
  private List<Transaction> transactions = new ArrayList<>();

  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  private Address address;

  public Account() {
  }

  public Account(Long id, Person person, String name, Double saldo, Address address) {
    this.id = id;
    this.person = person;
    this.name = name;
    this.saldo = saldo;
    this.address = address;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Branch getBranch() {
    return branch;
  }

  public void setBranch(Branch branch) {
    this.branch = branch;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
