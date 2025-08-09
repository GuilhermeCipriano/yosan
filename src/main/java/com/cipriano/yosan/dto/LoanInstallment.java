package com.cipriano.yosan.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class LoanInstallment {

    String installmentNumber;
    LocalDate date;
    String installmentValue; //valor total da parcela
    String taxValue; //juros
    String paidDebtValue; //amortizacao
    String remainingBalance; //saldo total restante


}
