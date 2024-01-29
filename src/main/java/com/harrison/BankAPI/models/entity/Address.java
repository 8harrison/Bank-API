package com.harrison.BankAPI.utils;

public class Address {

  private String rua;

  private Integer numero;

  private String cep;

  public Address() {
  }

  public Address(String rua, Integer numero, String cep) {
    this.rua = rua;
    this.numero = numero;
    this.cep = cep;
  }

  public String getRua() {
    return rua;
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
}
