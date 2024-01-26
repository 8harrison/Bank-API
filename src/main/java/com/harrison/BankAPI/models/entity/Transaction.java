package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Double valor;

  @ManyToOne
  @JsonIgnore
  private Account account;

  private Map<String, Account> accountMap;

  @Column(unique = true)
  private String code;

  public Transaction() {
  }

  public Transaction(Long id, String name, Double valor, Account account,
      Map<String, Account> accountMap) {
    this.id = id;
    this.name = name;
    this.valor = valor;
    this.account = account;
    this.accountMap = accountMap;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getValor() {
    return valor;
  }

  public void setValor(Double valor) {
    this.valor = valor;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Map<String, Account> getAccountMap() {
    return accountMap;
  }

  public void setAccountMap(
      Map<String, Account> accountMap) {
    this.accountMap = accountMap;
  }
}
