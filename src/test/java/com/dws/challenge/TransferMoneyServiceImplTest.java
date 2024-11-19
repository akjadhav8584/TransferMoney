package com.dws.challenge;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.impl.TransferMoneyServiceImpl;

public class TransferMoneyServiceImplTest {

    @InjectMocks
    private TransferMoneyServiceImpl transferMoneyService;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private AccountsService accountsService;

    private Account accountFrom;
    private Account accountTo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        accountFrom = new Account("123", BigDecimal.valueOf(1000));
        accountTo = new Account("456", BigDecimal.valueOf(500));
    }

    @Test
    public void testTransferMoney_Success() {
        when(accountsRepository.getAccount("123")).thenReturn(accountFrom);
        when(accountsRepository.getAccount("456")).thenReturn(accountTo);

        transferMoneyService.transferMoney("123", "456", BigDecimal.valueOf(200));

        verify(accountsRepository).save(accountFrom);
        verify(accountsRepository).save(accountTo);
        assertEquals(BigDecimal.valueOf(800), accountFrom.getBalance());
        assertEquals(BigDecimal.valueOf(700), accountTo.getBalance());
    }

    @Test
    public void testTransferMoney_InsufficientFunds() {
        when(accountsRepository.getAccount("123")).thenReturn(accountFrom);
        when(accountsRepository.getAccount("456")).thenReturn(accountTo);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferMoneyService.transferMoney("123", "456", BigDecimal.valueOf(2000));
        });

        assertEquals("Insufficient funds in account: 123", exception.getMessage());
    }

    @Test
    public void testTransferMoney_AccountNotFound() {
        when(accountsRepository.getAccount("123")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferMoneyService.transferMoney("123", "456", BigDecimal.valueOf(200));
        });

        assertEquals("Account not found: 123", exception.getMessage());
    }

    @Test
    public void testTransferMoney_SameAccount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferMoneyService.transferMoney("123", "123", BigDecimal.valueOf(200));
        });

        assertEquals("Source and destination accounts cannot be the same", exception.getMessage());
    }

    @Test
    public void testTransferMoney_InvalidAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferMoneyService.transferMoney("123", "456", BigDecimal.valueOf(0));
        });

        assertEquals("Invalid transfer request", exception.getMessage());
    }
}
