package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class PersonFixtures {

  public final static MockGen person_client = new MockGen(Map.of(
      "name", "Moacir Pitagoras",
      "email", "moacir.pit@hotmail.com",
      "cpf", "123.456.789-00",
      "username", "MoacirPitagoras",
      "password", "54321",
      "role", "CLIENT"
  ));

  public final static MockGen person_admin = new MockGen(Map.of(
      "name", "Janio Quadros",
      "cpf", "654.781.235-00",
      "email", "janio.quadros@hotmail.com",
      "username", "janioQua",
      "password", "789654",
      "role", "ADMIN"
  ));

  public final static MockGen person_client1 = new MockGen(Map.of(
      "username", "caliceJesus",
      "password", "123456",
      "cpf", "454.544.488-66",
      "email", "calice.jesus@hotmail.com",
      "name", "Calice de Jesus",
      "role", "CLIENT"
  ));

  public final static MockGen person_client2 = new MockGen(Map.of(
      "username", "juracicandongas",
      "password", "777777",
      "cpf", "777.777.777-77",
      "email", "juraci.candangas@hotmail.com",
      "name", "Juraci Candongas",
      "role", "CLIENT"
  ));

  public final static MockGen person_admin_1 = new MockGen(Map.of(
      "name", "João Goulart",
      "cpf", "456.545.789-66",
      "email", "jango@hotmail.com",
      "username", "jango",
      "password", "654123",
      "role", "ADMIN"
  ));

  public final static MockGen person_admin_2 = new MockGen(Map.of(
      "name", "Tarsila do Amaral",
      "cpf", "412.345.789-55",
      "email", "tarsila.ama@hotmail.com",
      "username", "tarsilaama",
      "password", "uhuhuh",
      "role", "ADMIN"
  ));

  public final static MockGen person_manager = new MockGen(Map.of(
      "name", "Machado de Assis",
      "cpf", "555.666.777-88",
      "email", "assis.machado@hotmail.com",
      "username", "machadassis",
      "password", "hfgdkflehf54594",
      "role", "MANAGER"
  ));
}
