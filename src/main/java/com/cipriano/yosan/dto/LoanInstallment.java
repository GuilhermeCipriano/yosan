package com.cipriano.yosan.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanInstallment {

    String installmentNumber;
    LocalDate date;
    BigDecimal installmentValue; //valor total da parcela
    BigDecimal taxValue; //juros
    BigDecimal paidDebtValue; //amortizacao
    BigDecimal remainingBalance; //saldo total restante


}
