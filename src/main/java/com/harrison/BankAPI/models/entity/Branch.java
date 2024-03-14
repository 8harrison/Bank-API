package com.harrison.BankAPI.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "branches")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "branch_people",
      joinColumns = @JoinColumn(name = "branch_id"),
      inverseJoinColumns = @JoinColumn(name = "person_id")
  )
  private List<Person> people = new ArrayList<>();

  @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL)
  private Address address;

  @Column(unique = true)
  private String code;

  @OneToMany(mappedBy = "branch", fetch = FetchType.EAGER)
  @JsonIgnore
  private List<Account> accounts = new ArrayList<>();

  @CreatedDate
  @JsonProperty("created_date")
  private LocalDate createdDate;

  @LastModifiedDate
  @JsonProperty("last_modified_date")
  private LocalDate lastModifiedDate;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;
}
