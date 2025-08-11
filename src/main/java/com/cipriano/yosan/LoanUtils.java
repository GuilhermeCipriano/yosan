package com.cipriano.yosan;
import com.cipriano.yosan.dto.LoanInstallment;
import com.cipriano.yosan.dto.request.LoanRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanUtils.class);

    public static ArrayList<LoanInstallment> calculateLoanSAC(LoanRequest loanRequest) {
        LOGGER.info("method::calculateLoanSAC called");
        LOGGER.info("data request: {}", loanRequest.toString());

        ArrayList<LoanInstallment> loanInstallmentList = new ArrayList<>();

        int months = getTotalMonthLoan(loanRequest);

        if (months <= 0) {
            LOGGER.warn("NO MONTHS FOUND, RETURNING EMPTY LIST");
            return new ArrayList<>();
        }

        BigDecimal monthsBigDecimal = BigDecimal.valueOf(months);
        BigDecimal monthlyAmortization = loanRequest.getLoanValue().divide(monthsBigDecimal, MONETARY_SCALE, RoundingMode.HALF_UP);
        BigDecimal remainingBalance = loanRequest.getLoanValue();
        BigDecimal gracePeriodDays = getGracePeriodDays(loanRequest);

        LocalDate currentPaymentDate = loanRequest.getFirstPaymentDate();

        LOGGER.info("VALUES FOUND, START LOAN CALCULATION");
        for (int installment = 0; installment < months; installment++) {
            int installmentNumber = installment + 1;

            BigDecimal taxValue;

            if (isLastInstallment(installment, FIRST_INSTALLMENT)) {
                LOGGER.info("method::isLastInstallment returned true");
                taxValue = getFirstMonthTax(loanRequest, remainingBalance, gracePeriodDays);
                LOGGER.info("method::getFirstMonthTax returned: {}", taxValue);
            } else {
                LOGGER.info("method::isLastInstallment returned false");
                // Para as demais, calcula juros mensais sobre o saldo devedor
                taxValue = getOtherMonthTax(loanRequest, remainingBalance);
                LOGGER.info("method::getOtherMonthTax returned: {}", taxValue);
            }

            LocalDate finalInstallmentDate = currentPaymentDate;

            if (isLastInstallment(installmentNumber, months)) {
                LOGGER.info("method::isLastInstallment returned true");
                monthlyAmortization = remainingBalance;
                finalInstallmentDate = loanRequest.getEndDate();

            }

            BigDecimal installmentValue = monthlyAmortization.add(taxValue);
            remainingBalance = remainingBalance.subtract(monthlyAmortization);

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
                LOGGER.info("method::isPaymentOnLastDayOfMonth returned true");
                currentPaymentDate = currentPaymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            } else {
                currentPaymentDate = currentPaymentDate.plusMonths(1);
            }
        }

        LOGGER.info("method::calculateLoanSAC end");
        return loanInstallmentList;
    }

    private static boolean isLastInstallment(int installmentNumber, int months) {
        LOGGER.info("method::isLastInstallment called");
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
        LOGGER.info("method::getFirstMonthTax called");
        return remainingBalance.multiply(
               getDailyTaxRate(loanRequest)
       ).multiply(gracePeriodDays);
    }

    private static BigDecimal getOtherMonthTax(LoanRequest loanRequest, BigDecimal remainingBalance) {

        return remainingBalance.multiply(
                getMonthlyTaxRate(loanRequest));
    }

    private static boolean isPaymentOnLastDayOfMonth(LoanRequest loanRequest) {
        LOGGER.info("method::isPaymentOnLastDayOfMonth called");
        return loanRequest.getFirstPaymentDate().getDayOfMonth() == loanRequest.getFirstPaymentDate().lengthOfMonth();
    }

    private static BigDecimal getGracePeriodDays(LoanRequest loanRequest) {
        LOGGER.info("method::getGracePeriodDays called");
        return new BigDecimal(ChronoUnit.DAYS.between(loanRequest.getInitialDate(), loanRequest.getFirstPaymentDate()));
    }

    private static int getTotalMonthLoan(LoanRequest loanRequest) {
        LOGGER.info("method::getTotalMonthLoan called");
        Period period = Period.between(loanRequest.getInitialDate(), loanRequest.getEndDate());
        return period.getYears() * MONTHS_IN_A_YEAR + period.getMonths();
    }
}