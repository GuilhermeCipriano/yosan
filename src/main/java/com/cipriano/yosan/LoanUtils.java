package com.cipriano.yosan;

import com.cipriano.yosan.dto.LoanInstallment;
import com.cipriano.yosan.dto.request.LoanRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public abstract class LoanUtils {

    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
    private static final DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
    public static final int MONTHS_IN_A_YEAR = 12;
    public static final int DAYS_IN_A_YEAR = 365;

    public static ArrayList<LoanInstallment>  calculateLoanSAC(LoanRequest loanRequest) {

        ArrayList<LoanInstallment> loanInstallmentList = new ArrayList<>();

        int months = getTotalMonthLoan(loanRequest);
        float[] installments = new float[months];

        float MonthlyPaidDebtValue = Float.parseFloat(String.valueOf(loanRequest.getLoanValue())) / (float) months;
        float remainingBalance = Float.parseFloat(String.valueOf(loanRequest.getLoanValue()));



        for (int installmentNumber = 0; installmentNumber < months; installmentNumber++) {
            float taxValue = 0f;

            if (installmentNumber == 0) {
                taxValue =  getDailyTaxValue(String.valueOf(loanRequest.getTaxRate()), remainingBalance, getTotalGraceDays(loanRequest));
            } else {
                taxValue = getMonthlyTaxValue(String.valueOf(loanRequest.getTaxRate()), remainingBalance);
            }

            float installmentValue = MonthlyPaidDebtValue + taxValue;

            installments[installmentNumber] = installmentValue;


            remainingBalance -= MonthlyPaidDebtValue;

            loanInstallmentList.add(
                    LoanInstallment.builder()
                            .installmentNumber(String.valueOf(installmentNumber+1))
                            .taxValue(BigDecimal.valueOf(taxValue))
                            .paidDebtValue(BigDecimal.valueOf(MonthlyPaidDebtValue))
                            .installmentValue(BigDecimal.valueOf(installmentValue))
                            .remainingBalance(BigDecimal.valueOf(remainingBalance))
                            .date(loanRequest.getFirstPaymentDate().plusMonths(installmentNumber))
                            .build()
            );

        }

        return loanInstallmentList;
    }

    public static String formatValue(double valor) {
        return df.format(valor);
    }

    private static float getDailyTaxValue(String taxRate, float remainingBalance, float gracePeriodDays) {
        return remainingBalance * (Float.parseFloat(taxRate) / 100.0F / DAYS_IN_A_YEAR) * gracePeriodDays;

    }

    private static float getMonthlyTaxValue(String taxRate, float remainingBalance) {
        return remainingBalance * (Float.parseFloat(taxRate) / 100.0F / MONTHS_IN_A_YEAR);

    }

    private static float getTotalGraceDays(LoanRequest loanRequest) {
        return (float) ChronoUnit.DAYS.between(loanRequest.getInitialDate(), loanRequest.getFirstPaymentDate());

    }

    private static int getTotalMonthLoan(LoanRequest loanRequest) {
        Period period = Period.between(loanRequest.getInitialDate(), loanRequest.getEndDate());
        return period.getYears() * MONTHS_IN_A_YEAR + period.getMonths();

    }
}
