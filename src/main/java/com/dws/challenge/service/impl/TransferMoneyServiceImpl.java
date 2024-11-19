package com.dws.challenge.service.impl;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.TransferMoneyService;

@Service
public class TransferMoneyServiceImpl implements TransferMoneyService {

    private final Lock lock = new ReentrantLock();

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsService accountsService;

    @Override
    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
        if (accountFromId == null || accountToId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer request");
        }
        if (accountFromId.equals(accountToId)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        lock.lock();
        try {
            Account accountFrom = accountsRepository.getAccount(accountFromId);
            if (accountFrom == null) {
                throw new IllegalArgumentException("Account not found: " + accountFromId);
            }

            Account accountTo = accountsRepository.getAccount(accountToId);
            if (accountTo == null) {
                throw new IllegalArgumentException("Account not found: " + accountToId);
            }

            if (accountFrom.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient funds in account: " + accountFromId);
            }

            accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
            accountTo.setBalance(accountTo.getBalance().add(amount));

            accountsRepository.save(accountFrom);
            accountsRepository.save(accountTo);
        } finally {
            lock.unlock();
        }
    }
}
