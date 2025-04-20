package com.gic.ui;

import com.gic.model.Transaction;
import com.gic.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class TransactionUITest {

    @InjectMocks
    private TransactionUI transactionUI;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Scanner scanner;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testProcessInputValidTransaction() {
        String inputLine = "20240503 ACC123 D 100.00";
        when(scanner.nextLine()).thenReturn(inputLine);
        when(transactionService.addTransaction(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new Transaction("TXN001", LocalDate.of(2024, 5, 3), 'D', new BigDecimal("100.00")));

        transactionUI.processInput(inputLine);

        verify(transactionService).addTransaction("20240503", "ACC123", "D", "100.00");
    }

    @Test
    void testProcessInputInvalidTransaction() {
        when(scanner.nextLine()).thenReturn("");
        transactionUI.start();

        verify(transactionService, never()).addTransaction(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testProcessInput() {
        Transaction txn = new Transaction("TXN001", LocalDate.of(2024, 4, 1), 'D', new BigDecimal("100.00"));
        when(transactionService.addTransaction("20240401", "ACC123", "D", "100.00")).thenReturn(txn);

        List<Transaction> txns = new ArrayList<>();
        txns.add(txn);
        when(transactionService.getAllTransactions("ACC123")).thenReturn(txns);

        transactionUI.processInput("20240401 ACC123 D 100.00");

        verify(transactionService, times(1)).addTransaction("20240401", "ACC123", "D", "100.00");
        verify(transactionService, times(1)).getAllTransactions("ACC123");
    }

    @Test
    void testProcessInputInvalidFormat() {
        transactionUI.processInput("20240401 ACC123 D");

        verify(transactionService, never()).addTransaction(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testProcessInputError() {
        when(transactionService.addTransaction("20240401", "ACC123", "D", "100.00")).thenThrow(new IllegalArgumentException("Invalid date format"));

        transactionUI.processInput("20240401 ACC123 D 100.00");

        verify(transactionService, times(1)).addTransaction("20240401", "ACC123", "D", "100.00");
    }
}
