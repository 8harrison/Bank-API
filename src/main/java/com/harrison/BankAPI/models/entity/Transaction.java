package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harrison.BankAPI.utils.TransactionTypes;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private TransactionTypes name;

  private Double valor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "account_id")
  private Account titular;

  @ManyToOne
  private Account recebedor;

  @Column(unique = true)
  private String code;

  @CreatedDate
  @JsonProperty("created_date")
  private LocalDate createdDate;

  public Transaction() {
  }

  public Transaction(Long id, TransactionTypes name, Double valor, Account titular) {
    this.id = id;
    this.name = name;
    this.valor = valor;
    this.titular = titular;
  }

  public Transaction(Long id, TransactionTypes name, Double valor, Account titular, Account recebedor) {
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

  public TransactionTypes getName() {
    return name;
  }

  public void setName(TransactionTypes name) {
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

  public LocalDate getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDate createdDate) {
    this.createdDate = createdDate;
  }
}
