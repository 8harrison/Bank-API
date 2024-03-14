package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.controller.dto.AccountDto.toAccount;
import static com.harrison.BankAPI.controller.dto.AccountDto.toDto;
import static com.harrison.BankAPI.controller.dto.CreationTransactioDto.toTransaction;
import static com.harrison.BankAPI.controller.dto.TransactionDto.toDto;

import com.harrison.BankAPI.controller.dto.AccountDto;
import com.harrison.BankAPI.controller.dto.CreationAccountDto;
import com.harrison.BankAPI.controller.dto.CreationTransactioDto;
import com.harrison.BankAPI.controller.dto.TransactionDto;
import com.harrison.BankAPI.exception.InvalidAcessException;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.service.AccountService;
import com.harrison.BankAPI.service.BranchService;
import com.harrison.BankAPI.service.PersonService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.harrison.BankAPI.utils.TransactionTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    private final PersonService personService;

    private final BranchService branchService;

    @Autowired
    public AccountController(AccountService accountService, PersonService personService,
                             BranchService branchService) {
        this.accountService = accountService;
        this.personService = personService;
        this.branchService = branchService;
    }

    @PostMapping
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<AccountDto> createAccount(@RequestBody CreationAccountDto dto) {
        Person person = personService.getByCpf(dto.cpf());
        Account account = new Account();
        account.setPerson(person);
        account.setName(dto.name());
        account.setSaldo(dto.saldo());
        Account saved = accountService.createAccount(account, dto.branchCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @GetMapping
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<AccountDto>> getAll() {
        List<Account> accounts = accountService.getAll();
        List<AccountDto> dtos = accounts.stream()
                .map(AccountDto::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        return ResponseEntity.ok(toDto(account));
    }

    @GetMapping("/find-by-code")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<AccountDto> getByCode(@RequestParam String code) {
        Account account = accountService.getByCode(code);
        return ResponseEntity.ok(toDto(account));
    }

    @PutMapping("/{id}")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<AccountDto> update(@RequestBody AccountDto dto, @PathVariable Long id) {
        String branchCode = dto.code().split("-")[0];
        Branch branch = branchService.getByCode(branchCode);
        Account account = accountService.updateAccount(id, toAccount(dto, branch));
        return ResponseEntity.ok(toDto(account));
    }

    @DeleteMapping("/{id}")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String message = accountService.deleteAccount(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}/address")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<AccountDto> setAddress(@PathVariable Long id,
                                                 @RequestBody Address address) {
        Account account = accountService.setAddress(id, address);
        return ResponseEntity.ok(toDto(account));
    }

    @PostMapping("/{id}/transactions")
    @Secured({"CLIENT"})
    public ResponseEntity<TransactionDto> createTransaction(@PathVariable Long id,
                                                            @RequestBody CreationTransactioDto dto) {
        Transaction transaction = toTransaction(dto);
        Account titular = accountService.getById(id);
        String pessoaLogada = personService.pessoaLogada();
        if (!titular.getPerson().getUsername().equals(pessoaLogada)) {
            throw new InvalidAcessException("Não é possível fazer transações na conta de outro usuário!");
        }
        Transaction created = accountService.createTransaction(id, transaction, dto.cpf());
        return ResponseEntity.status(201).body(toDto(created));
    }

    @GetMapping("/{id}/transactions")
    @Secured({"CLIENT", "MANAGER", "ADMIN"})
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable Long id) {
        Account titular = accountService.getById(id);
        String pessoaLogada = personService.pessoaLogada();
        if (!titular.getPerson().getUsername().equals(pessoaLogada)) {
            throw new InvalidAcessException("Não é possível acessar transações na conta de outro usuário!");
        }
        List<Transaction> transactions = accountService.getAllTransactions(id);
        List<TransactionDto> dtos = transactions.stream()
                .map(TransactionDto::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/transactions/period")
    @Secured({"CLIENT", "MANAGER", "ADMIN"})
    public ResponseEntity<List<TransactionDto>> getTransactionsByPeriod(@PathVariable Long id, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        List<Transaction> list = accountService.getTransactionsByPeriod(id, start, end);
        List<TransactionDto> dtos = list.stream()
                .map(TransactionDto::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{accountId}/transactions/{transactionId}")
    @Secured({"CLIENT", "MANAGER", "ADMIN"})
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long accountId,
                                                             @PathVariable Long transactionId) {
        Account titular = accountService.getById(accountId);
        String pessoaLogada = personService.pessoaLogada();
        if (!titular.getPerson().getUsername().equals(pessoaLogada)) {
            throw new InvalidAcessException("Não é possível acessar transações na conta de outro usuário!");
        }
        Transaction transaction = accountService.getTransactionById(accountId, transactionId);
        return ResponseEntity.ok(toDto(transaction));
    }

    @GetMapping("/{id}/transactions/find-by-code")
    @Secured({"CLIENT", "MANAGER", "ADMIN"})
    public ResponseEntity<TransactionDto> getTransactionByCode(@PathVariable Long id, @RequestParam String code) {
        Account titular = accountService.getById(id);
        String pessoaLogada = personService.pessoaLogada();
        if (!titular.getPerson().getUsername().equals(pessoaLogada)) {
            throw new InvalidAcessException("Não é possível acessar transações na conta de outro usuário!");
        }
        Transaction transaction = accountService.getTransactionByCode(id, code);
        return ResponseEntity.ok(toDto(transaction));
    }

    @DeleteMapping("/{accountId}/transactions/{transactionId}")
    @Secured({"MANAGER", "ADMIN"})
    public ResponseEntity<String> deleteTransaction(@PathVariable Long accountId, @PathVariable Long transactionId) {
        String message = accountService.deleteTransaction(accountId, transactionId);
        return ResponseEntity.ok(message);
    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

}
