package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.exception.InsulfficientFoundsException;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Bank;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

public class Calculator {

    private static Map<String, Account> deposito(Transaction transaction, Bank bank) {
        Account account = transaction.getTitular();
        Double saldo = round(account.getSaldo() + transaction.getValor() - bank.getDepositTax());
        account.setSaldo(saldo);
        return Map.of("titular", account);
    }

    private static Map<String, Account> saque(Transaction transaction, Bank bank) {
        Account account = transaction.getTitular();
        if (verificaFundos(account, transaction.getValor())) {
            Double saldo = round(account.getSaldo() - transaction.getValor() - bank.getDraftTax());
            account.setSaldo(saldo);
            return Map.of("titular", account);
        }
        throw new InsulfficientFoundsException("Saldo insulficiente!");
    }

    private static Map<String, Account> TED(Transaction transaction, Bank bank) {
        Account titular = transaction.getTitular();
        if (verificaFundos(titular, transaction.getValor())) {
            Double tSaldo = round(titular.getSaldo() - transaction.getValor() - bank.getTransferTax());
            titular.setSaldo(tSaldo);
            Account recebedor = transaction.getRecebedor();
            Double rSaldo = recebedor.getSaldo() + transaction.getValor();
            recebedor.setSaldo(rSaldo);
            return Map.of("titular", titular, "recebedor", recebedor);
        }
        throw new InsulfficientFoundsException("Saldo insulficiente!");
    }

    private static Map<String, Account> pix(Transaction transaction) {
        Account titular = transaction.getTitular();
        if (verificaFundos(titular, transaction.getValor())) {
            double tSaldo = round(titular.getSaldo() - transaction.getValor());
            titular.setSaldo(tSaldo);
            Account recebedor = transaction.getRecebedor();
            double rSaldo = round(recebedor.getSaldo() + transaction.getValor());
            recebedor.setSaldo(rSaldo);
            return Map.of("titular", titular, "recebedor", recebedor);
        }
        throw new InsulfficientFoundsException("Saldo Insulficiente!");
    }

    public static Map<String, Account> identifier(Transaction transaction, Bank bank) {
        return switch (transaction.getName()) {
            case DEPOSITO -> deposito(transaction, bank);
            case SAQUE -> saque(transaction, bank);
            case TED -> TED(transaction, bank);
            case PIX -> pix(transaction);
        };
    }

    private static boolean verificaFundos(Account account, Double valor) {
        return account.getSaldo() >= valor;
    }

    private static Double round(Double saldo) {
        return Math.round(saldo * 100.0) / 100.0;
    }

}
