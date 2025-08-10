package com.cipriano.yosan.dto.response;

import com.cipriano.yosan.dto.LoanInstallment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Builder
public class LoanResponse {

    BigDecimal loanValue;
    LocalDate initialDate;
    LocalDate endDate;
    LocalDate firstPaymentDate;
    BigDecimal taxRate;
    ArrayList<LoanInstallment> installmentList;

}
