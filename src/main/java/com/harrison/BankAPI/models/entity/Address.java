package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String rua;

  private Integer numero;

  private String cep;

  @OneToOne
  @JoinColumn(name = "branch_id")
  @JsonIgnore
  private Branch branch;

  @OneToOne
  @JoinColumn(name = "account_id")
  @JsonIgnore
  private Account account;

  public Address() {
  }

  public Address(Long id, String rua, Integer numero, String cep, Account account, Branch branch) {
    this.id = id;
    this.rua = rua;
    this.numero = numero;
    this.cep = cep;
    this.account = account;
    this.branch = branch;
  }

  public String getRua() {
    return rua;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRua(String rua) {
    this.rua = rua;
  }

  public Integer getNumero() {
    return numero;
  }

  public void setNumero(Integer numero) {
    this.numero = numero;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public Branch getBranch() {
    return branch;
  }

  public void setBranch(Branch branch) {
    this.branch = branch;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
