package com.cipriano.yosan;

import com.cipriano.yosan.dto.LoanInstallment;
import com.cipriano.yosan.dto.request.LoanRequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Period;
import java.util.ArrayList;
import java.util.Locale;

public abstract class LoanUtils {

    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
    private static final DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

    public static Float calculateLoanPrice(LoanRequest loanRequest) {
        Period period = Period.between(loanRequest.getEndDate(), loanRequest.getInitialDate());

        Float decimalTaxRate = Float.parseFloat(loanRequest.getTaxRate()) / 100.0F;
        Float factor = (float) Math.pow(1 + decimalTaxRate, period.getMonths());

        return Float.parseFloat(loanRequest.getLoanValue()) * (decimalTaxRate * factor) / (factor - 1);

    }

    public static ArrayList<LoanInstallment>  calculateLoanSAC(LoanRequest loanRequest) {
        Period period = Period.between(loanRequest.getInitialDate(), loanRequest.getEndDate());
        ArrayList<LoanInstallment> loanInstallmentList = new ArrayList<>();
        int months = period.getYears() * 12 + period.getMonths();
        float[] installments = new float[months];
        float paidDebtValue = Float.parseFloat(loanRequest.getLoanValue()) / (float) months;
        float remainingBalance = Float.parseFloat(loanRequest.getLoanValue());

//        todo: double firstInstallmente = ...

        for (int installmentNumber = 1; installmentNumber <= months; installmentNumber++) {

            float taxValue = remainingBalance * (Float.parseFloat(loanRequest.getTaxRate()) / 100.0F / 12);
            float installmentValue = paidDebtValue + taxValue;
            installments[installmentNumber] = installmentValue;

            remainingBalance -= paidDebtValue;

            loanInstallmentList.add(
                    LoanInstallment.builder()
                            .installmentNumber(String.valueOf(installmentNumber))
                            .taxValue(formatValue(taxValue))
                            .paidDebtValue(formatValue(paidDebtValue))
                            .installmentValue(formatValue(installmentValue))
                            .remainingBalance(formatValue(remainingBalance))
                            .date(loanRequest.getFirstPaymentDate().plusMonths(installmentNumber))
                            .build()
            );

        }

        return loanInstallmentList;
    }

    public static String formatValue(double valor) {
        return df.format(valor);
    }


}
