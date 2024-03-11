package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.harrison.BankAPI.utils.AccountTypes;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
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

  private AccountTypes name;

  private Double saldo;

  @Column(unique = true)
  private String code;

  @OneToMany(mappedBy = "titular", fetch = FetchType.EAGER)
  @JsonIgnore
  private List<Transaction> transactions = new ArrayList<>();

  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  private Address address;

  @CreatedDate
  @JsonProperty("created_date")
  private LocalDate createdDate;

  @LastModifiedDate
  @JsonProperty("last_modified_date")
  private LocalDate lastModifiedDate;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String modifiedBy;

  private LocalDate lastIncome;

  public Account() {
  }

  public Account(Long id, Person person, AccountTypes name, Double saldo, Address address) {
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

  public AccountTypes getName() {
    return name;
  }

  public void setName(AccountTypes name) {
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

  public LocalDate getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDate createdDate) {
    this.createdDate = createdDate;
  }

  public LocalDate getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(LocalDate lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public LocalDate getLastIncome() {
    return lastIncome;
  }

  public void setLastIncome(LocalDate lastIncome) {
    this.lastIncome = lastIncome;
  }
}
