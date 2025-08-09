package com.cipriano.yosan.dto.response;

import com.cipriano.yosan.dto.LoanInstallment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Builder
public class LoanResponse {

    String loanValue;
    LocalDate initialDate;
    LocalDate endDate;
    LocalDate firstPaymentDate;
    String taxRate;
    ArrayList<LoanInstallment> installmentList;

}
