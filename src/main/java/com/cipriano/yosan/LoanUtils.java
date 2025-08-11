package com.cipriano.yosan;

import com.cipriano.yosan.dto.LoanInstallment;
import com.cipriano.yosan.dto.request.LoanRequest;

import java.math.BigDecimal;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public abstract class LoanUtils {
    public static final int MONTHS_IN_A_YEAR = 12;
    public static final BigDecimal BIG_DECIMAL_MONTHS_IN_A_YEAR = new BigDecimal(12);
    public static final BigDecimal BIG_DECIMAL_DAYS_IN_A_YEAR = new BigDecimal(365);
    public static final int MONETARY_SCALE = 2;
    public static final int TAX_SCALE = 10;
    public static final BigDecimal BIG_DECIMAL_TAX_RATE_BASE = new BigDecimal(100);
    public static final int FIRST_INSTALLMENT = 0;

    public static ArrayList<LoanInstallment> calculateLoanSAC(LoanRequest loanRequest) {
        ArrayList<LoanInstallment> loanInstallmentList = new ArrayList<>();

        int months = getTotalMonthLoan(loanRequest);

        if (months <= 0) {
            return new ArrayList<>();
        }

        BigDecimal monthsBigDecimal = BigDecimal.valueOf(months);
        BigDecimal monthlyAmortization = loanRequest.getLoanValue().divide(monthsBigDecimal, MONETARY_SCALE, RoundingMode.HALF_UP);
        BigDecimal remainingBalance = loanRequest.getLoanValue();
        BigDecimal gracePeriodDays = getGracePeriodDays(loanRequest);

        LocalDate currentPaymentDate = loanRequest.getFirstPaymentDate();

        for (int installment = 0; installment < months; installment++) {
            int installmentNumber = installment + 1;

            BigDecimal taxValue;

            if (isLastInstallment(installment, FIRST_INSTALLMENT)) {
                taxValue = getFirstMonthTax(loanRequest, remainingBalance, gracePeriodDays);
            } else {
                taxValue = getOtherMonthTax(loanRequest, remainingBalance, gracePeriodDays);
            }

            if (isLastInstallment(installmentNumber, months)) {
                monthlyAmortization = remainingBalance;
            }

            BigDecimal installmentValue = monthlyAmortization.add(taxValue);
            remainingBalance = remainingBalance.subtract(monthlyAmortization);
            LocalDate finalInstallmentDate = currentPaymentDate;

            if (isLastInstallment(installmentNumber, months)) {
                finalInstallmentDate = loanRequest.getEndDate();
            }

            loanInstallmentList.add(
                    LoanInstallment.builder()
                            .installmentNumber(String.valueOf(installmentNumber))
                            .taxValue(taxValue)
                            .paidDebtValue(monthlyAmortization)
                            .installmentValue(installmentValue)
                            .remainingBalance(remainingBalance)
                            .date(finalInstallmentDate)
                            .build()
            );

            if (isPaymentOnLastDayOfMonth(loanRequest)) {
                currentPaymentDate = currentPaymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            } else {
                currentPaymentDate = currentPaymentDate.plusMonths(1);
            }
        }

        return loanInstallmentList;
    }

    private static boolean isLastInstallment(int installmentNumber, int months) {
        return installmentNumber == months;
    }

    private static BigDecimal getMonthlyTaxRate(LoanRequest loanRequest) {
        return loanRequest.getTaxRate()
                .divide(BIG_DECIMAL_TAX_RATE_BASE, TAX_SCALE, RoundingMode.HALF_UP)
                .divide(BIG_DECIMAL_MONTHS_IN_A_YEAR, TAX_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal getDailyTaxRate(LoanRequest loanRequest) {
       return loanRequest.getTaxRate()
                .divide(BIG_DECIMAL_TAX_RATE_BASE, TAX_SCALE, RoundingMode.HALF_UP)
                .divide(BIG_DECIMAL_DAYS_IN_A_YEAR, TAX_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal getFirstMonthTax(LoanRequest loanRequest, BigDecimal remainingBalance, BigDecimal gracePeriodDays) {
       return remainingBalance.multiply(
               getDailyTaxRate(loanRequest)
       ).multiply(gracePeriodDays);
    }

    private static BigDecimal getOtherMonthTax(LoanRequest loanRequest, BigDecimal remainingBalance, BigDecimal gracePeriodDays) {
        return remainingBalance.multiply(
                getMonthlyTaxRate(loanRequest)
        ).multiply(gracePeriodDays);
    }

    private static boolean isPaymentOnLastDayOfMonth(LoanRequest loanRequest) {
        return loanRequest.getFirstPaymentDate().getDayOfMonth() == loanRequest.getFirstPaymentDate().lengthOfMonth();
    }

    private static BigDecimal getGracePeriodDays(LoanRequest loanRequest) {
        return new BigDecimal(ChronoUnit.DAYS.between(loanRequest.getInitialDate(), loanRequest.getFirstPaymentDate()));
    }

    private static int getTotalMonthLoan(LoanRequest loanRequest) {
        Period period = Period.between(loanRequest.getInitialDate(), loanRequest.getEndDate());
        return period.getYears() * MONTHS_IN_A_YEAR + period.getMonths();
    }
}