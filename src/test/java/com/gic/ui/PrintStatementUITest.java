package com.gic.ui;

import com.gic.service.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Scanner;

import static org.mockito.Mockito.*;

class PrintStatementUITest {

    @InjectMocks
    private PrintStatementUI printStatementUI;

    @Mock
    private StatementService statementService;

    @Mock
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessInputValidStatementRequest() {
        String inputLine = "ACC123 202404";
        when(scanner.nextLine()).thenReturn(inputLine);
        doNothing().when(statementService).printStatement(anyString(), anyString());

        printStatementUI.processInput(inputLine);

        verify(statementService).printStatement("ACC123", "202404");
    }

    @Test
    void testProcessInputInvalidStatementRequest() {
        String inputLine = "";
        when(scanner.nextLine()).thenReturn(inputLine);
        doNothing().when(statementService).printStatement(anyString(), anyString());

        printStatementUI.processInput(inputLine);

        verify(statementService, never()).printStatement(anyString(), anyString());
    }

    @Test
    void testProcessInput() {
        String accountId = "ACC123";
        String yearMonth = "202404";

        printStatementUI.processInput(accountId + " " + yearMonth);

        verify(statementService, times(1)).printStatement(accountId, yearMonth);
    }

    @Test
    void testProcessInputInvalidFormat() {
        String input = "ACC123";

        printStatementUI.processInput(input);

        verify(statementService, never()).printStatement(anyString(), anyString());
    }
}
