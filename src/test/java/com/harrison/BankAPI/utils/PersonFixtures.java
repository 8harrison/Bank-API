package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class PersonFixtures {

  public final static MockGen person_client = new MockGen(Map.of(
      "name", "Moacir Pitagoras",
      "email", "moacir.pit@hotmail.com",
      "cpf", "12345678",
      "username", "MoacirPitagoras",
      "password", "54321",
      "role", "CLIENT"
  ));

  public final static MockGen person_admin = new MockGen(Map.of(
      "name", "Janio Quadros",
      "cpf", "65478123",
      "email", "janio.quadros@hotmail.com",
      "username", "janioQua",
      "password", "789654",
      "role", "ADMIN"
  ));

  public final static MockGen person_client1 = new MockGen(Map.of(
      "username", "caliceJesus",
      "password", "123456",
      "cpf", "45454448866",
      "email", "calice.jesus@hotmail.com",
      "name", "Calice de Jesus",
      "role", "CLIENT"
  ));

  public final static MockGen person_client2 = new MockGen(Map.of(
      "username", "juracicandongas",
      "password", "777777",
      "cpf", "77777777777",
      "email", "juraci.candangas@hotmail.com",
      "name", "Juraci Candongas",
      "role", "CLIENT"
  ));

  public final static MockGen person_admin_1 = new MockGen(Map.of(
      "name", "João Goulart",
      "cpf", "45654578966",
      "email", "jango@hotmail.com",
      "username", "jango",
      "password", "654123",
      "role", "ADMIN"
  ));

  public final static MockGen person_admin_2 = new MockGen(Map.of(
      "name", "Tarsila do Amaral",
      "cpf", "41234578955",
      "email", "tarsila.ama@hotmail.com",
      "username", "tarsilaama",
      "password", "uhuhuh",
      "role", "ADMIN"
  ));

  public final static MockGen person_manager = new MockGen(Map.of(
      "name", "Machado de Assis",
      "cpf", "55566677788",
      "email", "assis.machado@hotmail.com",
      "username", "machadassis",
      "password", "hfgdkflehf54594",
      "role", "MANAGER"
  ));
}
