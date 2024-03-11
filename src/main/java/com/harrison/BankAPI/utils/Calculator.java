package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.exception.InsulfficientFoundsException;
import com.harrison.BankAPI.exception.InvalidTransactionException;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Transaction;

import java.util.Map;

public class Calculator {

    private static Map<String, Account> deposito(Transaction transaction) {
        Account account = transaction.getTitular();
        account.setSaldo(account.getSaldo() + transaction.getValor());
        return Map.of("titular", account);
    }

    private static Map<String, Account> saque(Transaction transaction) {
        Account account = transaction.getTitular();
        if (verificaFundos(account, transaction.getValor())) {
            account.setSaldo(account.getSaldo() - transaction.getValor());
            return Map.of("titular", account);
        }
        throw new InsulfficientFoundsException("Saldo insulficiente!");
    }

    private static Map<String, Account> transferencia(Transaction transaction) {
        Account titular = transaction.getTitular();
        if (verificaFundos(titular, transaction.getValor())) {
            titular.setSaldo(titular.getSaldo() - transaction.getValor());
            Account recebedor = transaction.getRecebedor();
            recebedor.setSaldo(recebedor.getSaldo() + transaction.getValor());
            return Map.of("titular", titular, "recebedor", recebedor);
        }
        throw new InsulfficientFoundsException("Saldo insulficiente!");
    }

    private static Map<String, Account> pix(Transaction transaction) {
        Account titular = transaction.getTitular();
        if (verificaFundos(titular, transaction.getValor())) {
            titular.setSaldo(titular.getSaldo() - transaction.getValor());
            Account recebedor = transaction.getRecebedor();
            recebedor.setSaldo(recebedor.getSaldo() + transaction.getValor());
            return Map.of("titular", titular, "recebedor", recebedor);
        }
        throw new InsulfficientFoundsException("Saldo Insulficiente!");
    }

    public static Map<String, Account> identifier(Transaction transaction) {
        return switch (transaction.getName()) {
            case DEPOSITO -> deposito(transaction);
            case SAQUE -> saque(transaction);
            case DOC -> transferencia(transaction);
            case TED -> null;
            case PIX -> pix(transaction);
        };
    }

    private static boolean verificaFundos(Account account, Double valor) {
        return account.getSaldo() >= valor;
    }

}
