package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.*;
import com.harrison.BankAPI.models.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.harrison.BankAPI.utils.Calculator.identifier;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final BankRepository bankRepository;
    private final BranchRepository branchRepository;
    private final PersonRepository personRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AddressRepository addressRepository,
                          BankRepository bankRepository,
                          BranchRepository branchRepository,
                          PersonRepository personRepository,
                          TransactionRepository transactionRepository
                          ) {
        this.accountRepository = accountRepository;
        this.addressRepository = addressRepository;
        this.bankRepository = bankRepository;
        this.branchRepository = branchRepository;
        this.personRepository = personRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(Account account, String branchCode) {
        return setCode(account, branchCode);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getById(Long id) {
        return verifyAccount(id);
    }

    public Account getByCode(String code) {
        Optional<Account> account = accountRepository.findByCode(code);

        if (account.isEmpty()) {
            throw new NotFoundException(String.format("Conta de código %s não encontrada!", code));
        }
        return account.get();
    }

    public Account updateAccount(Long id, Account account) {
        String branchCode = account.getBranch().getCode();
        Account founded = verifyAccount(id);
        account.setCreatedDate(founded.getCreatedDate());
        account.setCreatedBy(founded.getCreatedBy());
        Branch branch = verifyBranch(branchCode);
        account.setCode(founded.getCode());
        account.setSaldo(founded.getSaldo());
        account.setId(id);
        branch.getAccounts().add(account);
        account.setPerson(founded.getPerson());
        branchRepository.save(branch);
        return setCode(account, branchCode);
    }

    public String deleteAccount(Long id) {
        verifyAccount(id);
        accountRepository.deleteById(id);
        return "Conta excluída com sucesso!";
    }

    public Transaction createTransaction(Long id, Transaction transaction, String cpf) {
        Account titular = verifyAccount(id);
        transaction.setTitular(titular);
        if (cpf != null) {
            Person person = verifyCpf(cpf);
            Account recebedor = person.getAccount();
            transaction.setRecebedor(recebedor);
        }
        return setCode(transaction);
    }

    public Transaction getTransactionById(Long accountId, Long transactionId) {
        verifyAccount(accountId);
        return verifyTransaction(transactionId);
    }

    public Transaction getTransactionByCode(Long accountId, String code) {
        verifyAccount(accountId);
        Optional<Transaction> transaction = transactionRepository.findByCode(code);
        if (transaction.isEmpty()) {
            throw new NotFoundException("Transação de código %s não encontrada!".formatted(code));
        }
        return transaction.get();
    }

    public List<Transaction> getAllTransactions(Long accountId) {
        return verifyAccount(accountId).getTransactions();
    }

    public List<Transaction> getTransactionsByPeriod(Long accountId, LocalDate start, LocalDate end) {
        return transactionRepository.getTransactionsByPeriod(accountId, start, end);
    }

    public String deleteTransaction(Long accountId, Long transactionId) {
        verifyAccount(accountId);
        verifyTransaction(transactionId);
        transactionRepository.deleteById(transactionId);
        return "Transação excluída com sucesso!";
    }

    public Account setAddress(Long id, Address address) {
        Account account = verifyAccount(id);
        addressRepository.save(address);
        account.setAddress(address);
        return accountRepository.save(account);
    }

    public Account verifyAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException(String.format("Conta de id %s não encontrada!", id));
        }
        return account.get();
    }

    private Person verifyPerson(Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isEmpty()) {
            throw new NotFoundException("Pessoa de id %s não encontrada".formatted(id));
        }
        return person.get();
    }

    private Person verifyCpf(String cpf) {
        Optional<Person> person = personRepository.findByCpf(cpf);
        if (person.isEmpty()) {
            throw new NotFoundException("Pessoa de CPF %s não encontrada!".formatted(cpf));
        }
        return person.get();
    }

    private Transaction verifyTransaction(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new NotFoundException("Transação de id %s não encontrada!".formatted(id));
        }
        return transaction.get();
    }

    private Branch verifyBranch(String code) {
        Optional<Branch> branch = branchRepository.findByCode(code);
        if (branch.isEmpty()) {
            throw new NotFoundException("Agência de código %s não encontrada".formatted(code));
        }
        return branch.get();
    }

    private Bank verifyBank(Long id) {
        Optional<Bank> optional = bankRepository.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException("Banco Não Encontrado!");
        }
        return optional.get();
    }

    private String generateCode(Account account) {
        int num = account.getBranch().getAccounts().size() - 1;
        if (num >= 0) {
            String lastCode = account.getBranch().getAccounts().get(num).getCode();
            String[] serial = lastCode.split("[^0-9]");
            num = Integer.parseInt(serial[serial.length - 1]) + 1;
        } else {
            num = 1;
        }
        StringBuilder code = new StringBuilder("" + num);
        while (code.length() < 7) {
            code.insert(0, "0");
        }
        return code.toString();
    }

    private String generateCode(Transaction transaction) {
        List<Transaction> test = transaction.getTitular().getTransactions();
        int num = transaction.getTitular().getTransactions().size() - 1;
        if (num >= 0) {
            String lastCode = transaction.getTitular().getTransactions().get(num).getCode();
            String[] serial = lastCode.split("[^0-9]");
            num = Integer.parseInt(serial[serial.length - 1]) + 1;
        } else {
            num = 1;
        }
        StringBuilder code = new StringBuilder("" + num);
        while (code.length() < 7) {
            code.insert(0, "0");
        }
        return code.toString();
    }

    private Account setCode(Account account, String branchCode) {
        verifyPerson(account.getPerson().getId());
        Account created = accountRepository.save(account);
        Branch branch = verifyBranch(branchCode);
        created.setBranch(branch);
        String code = generateCode(account);
        code = account.getBranch().getCode() + "-" + code;
        created.setCode(code);
        return accountRepository.save(created);
    }

    private Transaction setCode(Transaction transaction) {
        Bank bank = verifyBank(1L);
        Map<String, Account> accounts = identifier(transaction, bank);
        String code = generateCode(transaction);
        String letter = transaction.getName().name().substring(0, 2);
        code += "-" + letter;
        transaction.setCode(code);
        accounts.values().forEach(account -> {
            account.getTransactions().add(transaction);
            accountRepository.save(account);
        });
        return transactionRepository.save(transaction);
    }
}
