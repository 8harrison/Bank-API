package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.harrison.BankAPI.utils.AccountTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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

}
