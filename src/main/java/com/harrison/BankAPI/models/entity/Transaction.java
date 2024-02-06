package com.harrison.BankAPI.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Double valor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "account_id")
  private Account titular;

  @ManyToOne
  private Account recebedor;

  @Column(unique = true)
  private String code;

  public Transaction() {
  }

  public Transaction(Long id, String name, Double valor, Account titular) {
    this.id = id;
    this.name = name;
    this.valor = valor;
    this.titular = titular;
  }

  public Transaction(Long id, String name, Double valor, Account titular, Account recebedor) {
    this.id = id;
    this.name = name;
    this.valor = valor;
    this.titular = titular;
    this.recebedor = recebedor;
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

  public Account getTitular() {
    return titular;
  }

  public void setTitular(Account titular) {
    this.titular = titular;
  }

  public Account getRecebedor() {
    return recebedor;
  }

  public void setRecebedor(Account recebedor) {
    this.recebedor = recebedor;
  }
}
