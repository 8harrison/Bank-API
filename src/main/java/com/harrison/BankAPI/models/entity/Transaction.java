package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harrison.BankAPI.utils.TransactionTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

}
