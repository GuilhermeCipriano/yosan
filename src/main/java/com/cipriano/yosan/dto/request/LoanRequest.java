package com.cipriano.yosan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanRequest {

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate initialDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate endDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate firstPaymentDate;
    String loanValue;
    String taxRate;
}
