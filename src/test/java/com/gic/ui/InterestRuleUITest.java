package com.gic.ui;

import com.gic.service.InterestRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InterestRuleUITest {

    @InjectMocks
    private InterestRuleUI interestRuleUI;

    @Mock
    private InterestRuleService interestRuleService;

    @Mock
    private Scanner scanner;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testProcessInputValidInterestRule() {
        String inputLine = "20240503 RULE001 1.5";
        when(scanner.nextLine()).thenReturn(inputLine);
        doNothing().when(interestRuleService).addOrUpdateInterestRule(anyString(), anyString(), anyString());

        interestRuleUI.processInput(inputLine);

        verify(interestRuleService).addOrUpdateInterestRule("20240503", "RULE001", "1.5");
    }

    @Test
    void testProcessInputInvalidFormat() {
        String inputLine = "20240503 RULE001";
        interestRuleUI.processInput(inputLine);
        assertEquals("Invalid input format. Please enter exactly 3 fields.", outContent.toString().trim());
    }

    @Test
    void testProcessInputServiceThrowsException() {
        String inputLine = "20240503 RULE001 1.5";
        doThrow(new IllegalArgumentException("Invalid date format. Use YYYYMMdd.")).when(interestRuleService).addOrUpdateInterestRule(anyString(), anyString(), anyString());

        interestRuleUI.processInput(inputLine);

        assertEquals("Error: Invalid date format. Use YYYYMMdd.", outContent.toString().trim());
    }
}
