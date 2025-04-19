package com.gic.service;

import com.gic.model.Account;
import com.gic.model.InterestRule;
import com.gic.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class StatementService {

    private final TransactionService transactionService;
    private final InterestRuleService interestRuleService;

    public StatementService(TransactionService transactionService, InterestRuleService interestRuleService) {
        this.transactionService = transactionService;
        this.interestRuleService = interestRuleService;
    }

    public void printStatement(String accountId, String yearMonth) {
        Account account = transactionService.getAccount(accountId);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(yearMonth, java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        } catch (Exception e) {
            System.out.println("Invalid year-month format.");
            return;
        }

        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        // get transactions in the month
        List<Transaction> monthlyTxns = account.getTransactionsForMonth(yearMonth);

        // calculate daily balances for the month
        Map<LocalDate, BigDecimal> dailyBalances = calculateDailyBalances(account, startDate, endDate);

        // calculate interest
        List<InterestSegment> interestSegments = calculateInterestSegments(dailyBalances, startDate, endDate);

        BigDecimal totalInterest = BigDecimal.ZERO;
        for (InterestSegment seg : interestSegments) {
            totalInterest = totalInterest.add(seg.interest);
        }

        // annualize interest
        totalInterest = totalInterest.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        totalInterest = totalInterest.setScale(2, RoundingMode.HALF_UP);

        // add interest transaction on last day of month if interest > 0
        if (totalInterest.compareTo(BigDecimal.ZERO) > 0) {
            // Add interest transaction to monthlyTxns for printing only (not stored permanently)
            Transaction interestTxn = new Transaction("", endDate, 'I', totalInterest);
            monthlyTxns.add(interestTxn);
        }

        // print statement header
        System.out.println("\nAccount: " + accountId);
        System.out.println("| Date     | Txn Id      | Type | Amount | Balance |");

        // calculate running balance for the month including interest
        BigDecimal runningBalance = getBalanceBeforeDate(account, startDate);
        monthlyTxns.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTxnId, Comparator.nullsLast(String::compareTo)));

        for (Transaction txn : monthlyTxns) {
            if (txn.getType() == 'D' || txn.getType() == 'I') {
                runningBalance = runningBalance.add(txn.getAmount());
            } else if (txn.getType() == 'W') {
                runningBalance = runningBalance.subtract(txn.getAmount());
            }
            String txnId = txn.getTxnId() == null ? "" : txn.getTxnId();
            System.out.printf("| %s | %-11s | %c    | %6.2f | %7.2f |\n",
                    txn.getDate().toString().replaceAll("-", ""),
                    txnId,
                    txn.getType(),
                    txn.getAmount(),
                    runningBalance);
        }
    }

    private Map<LocalDate, BigDecimal> calculateDailyBalances(Account account, LocalDate start, LocalDate end) {
        Map<LocalDate, BigDecimal> dailyBalances = new LinkedHashMap<>();
        BigDecimal balance = getBalanceBeforeDate(account, start);

        // get all transactions in the period
        NavigableMap<LocalDate, List<Transaction>> txnsInPeriod = new TreeMap<>();
        for (Transaction txn : account.getAllTransactions()) {
            if (!txn.getDate().isBefore(start) && !txn.getDate().isAfter(end)) {
                txnsInPeriod.computeIfAbsent(txn.getDate(), d -> new ArrayList<>()).add(txn);
            }
        }

        LocalDate current = start;
        while (!current.isAfter(end)) {
            List<Transaction> txns = txnsInPeriod.getOrDefault(current, Collections.emptyList());
            for (Transaction txn : txns) {
                if (txn.getType() == 'D' || txn.getType() == 'I') {
                    balance = balance.add(txn.getAmount());
                } else if (txn.getType() == 'W') {
                    balance = balance.subtract(txn.getAmount());
                }
            }
            dailyBalances.put(current, balance);
            current = current.plusDays(1);
        }
        return dailyBalances;
    }

    private List<InterestSegment> calculateInterestSegments(Map<LocalDate, BigDecimal> dailyBalances, LocalDate start, LocalDate end) {
        List<InterestSegment> segments = new ArrayList<>();

        if (dailyBalances.isEmpty()) return segments;

        // get all interest rule changes in the period
        NavigableMap<LocalDate, InterestRule> rulesInPeriod = interestRuleService.getRulesBetween(start, end);
        if (rulesInPeriod.isEmpty()) {
            // Try to get latest rule before start
            InterestRule prevRule = interestRuleService.getRuleForDate(start);
            if (prevRule == null) {
                // No rules defined, no interest
                return segments;
            } else {
                rulesInPeriod = new TreeMap<>();
                rulesInPeriod.put(start, prevRule);
            }
        }

        // build list of dates where rule changes occur
        List<LocalDate> changeDates = new ArrayList<>(rulesInPeriod.keySet());
        if (!changeDates.contains(start)) {
            changeDates.add(0, start);
        }
        if (!changeDates.contains(end.plusDays(1))) {
            changeDates.add(end.plusDays(1));
        }
        Collections.sort(changeDates);

        for (int i = 0; i < changeDates.size() - 1; i++) {
            LocalDate segStart = changeDates.get(i);
            LocalDate segEnd = changeDates.get(i + 1).minusDays(1);
            if (segEnd.isBefore(start)) continue;
            if (segStart.isAfter(end)) break;
            if (segStart.isBefore(start)) segStart = start;
            if (segEnd.isAfter(end)) segEnd = end;

            InterestRule rule = interestRuleService.getRuleForDate(segStart);
            if (rule == null) continue;

            // calculate sum of (balance * days)
            BigDecimal sumBalanceDays = BigDecimal.ZERO;
            int numDays = 0;
            LocalDate d = segStart;
            while (!d.isAfter(segEnd)) {
                BigDecimal bal = dailyBalances.getOrDefault(d, BigDecimal.ZERO);
                sumBalanceDays = sumBalanceDays.add(bal);
                numDays++;
                d = d.plusDays(1);
            }

            // interest calculation
            BigDecimal ratePercent = rule.getRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            BigDecimal interest = sumBalanceDays.multiply(ratePercent);

            segments.add(new InterestSegment(segStart, segEnd, numDays, rule.getRuleId(), rule.getRate(), interest));
        }
        return segments;
    }

    private BigDecimal getBalanceBeforeDate(Account account, LocalDate date) {
        // balance at date-1
        LocalDate prevDate = date.minusDays(1);
        return account.getBalanceAtDate(prevDate);
    }

    private static class InterestSegment {
        LocalDate start;
        LocalDate end;
        int numDays;
        String ruleId;
        BigDecimal rate;
        BigDecimal interest;

        InterestSegment(LocalDate start, LocalDate end, int numDays, String ruleId, BigDecimal rate, BigDecimal interest) {
            this.start = start;
            this.end = end;
            this.numDays = numDays;
            this.ruleId = ruleId;
            this.rate = rate;
            this.interest = interest;
        }
    }
}
