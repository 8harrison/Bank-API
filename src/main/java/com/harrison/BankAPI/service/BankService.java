package com.harrison.BankAPI.service;

import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Bank;
import com.harrison.BankAPI.models.repository.AccountRepository;
import com.harrison.BankAPI.models.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final AccountRepository accountRepository;
    @Value("${api.actual-db}")
    private String actualDb;

    @Autowired
    public BankService(BankRepository bankRepository, AccountRepository accountRepository) {
        this.bankRepository = bankRepository;
        this.accountRepository = accountRepository;
    }


    public Bank create(Bank bank) {
        Bank created = bankRepository.save(bank);
        updateIncome(created.getId());
        return created;
    }

    public Bank updateTax(Long id, Bank bank) {
        Bank founded = verifyBank(id);
        return bankRepository.save(bank);
    }

    public Bank getById(Long id) {
        return verifyBank(id);
    }

    private void updateIncome(Long id) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Bank bank = verifyBank(id);
                Double tax = bank.getIncomeTax();
                List<Account> accounts = accountRepository.findAll();
                updateAccounts(accounts, tax);
            }
        };
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        LocalTime actual = LocalTime.now();
        long delay = ((24L - actual.getHour()) * 60) + (60L - actual.getMinute());
        long period = (24L * 60) - delay;
        if (actualDb.equals("test")) {
            delay = 0;
            period = 1;
        }
        executor.scheduleAtFixedRate(task, delay, period, TimeUnit.MINUTES);
    }

    private void updateAccounts(List<Account> accounts, Double tax) {
        accounts.forEach(account -> {
            LocalDate date;
            if (account.getLastIncome() != null) {
                date = account.getLastIncome().plusMonths(1);
            } else {
                date = account.getCreatedDate().plusMonths(1);
            }
            updateBalance(date, account, tax);
        });
    }

    private Bank verifyBank(Long id) {
        Optional<Bank> optional = bankRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Banco não encontrado!");
        }
        return optional.get();
    }

    private void updateBalance(LocalDate date, Account account, Double tax) {
        LocalDate actual = LocalDate.now();
        if (actualDb.equals("test")) {
            LocalTime actualTime = LocalTime.now();
            LocalTime createdTime = LocalTime.of(0, 0);
            if (actualTime.isAfter(createdTime)) {
                Double saldo = account.getSaldo();
                saldo *= (1 + tax);
                BigDecimal bd = new BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN);
                account.setSaldo(bd.doubleValue());
                accountRepository.save(account);
            }
        }
        if (date.isEqual(actual)) {
            Double saldo = account.getSaldo();
            saldo *= (1 + tax);
            BigDecimal bd = new BigDecimal(saldo).setScale(2, RoundingMode.HALF_EVEN);
            account.setSaldo(bd.doubleValue());
            accountRepository.save(account);
        }
    }
}
