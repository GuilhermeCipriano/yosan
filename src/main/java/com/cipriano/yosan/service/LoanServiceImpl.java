package com.cipriano.yosan.service;

import com.cipriano.yosan.LoanUtils;
import com.cipriano.yosan.dto.request.LoanRequest;
import com.cipriano.yosan.dto.response.LoanResponse;
import com.cipriano.yosan.service.interfaces.LoanServiceInterface;
import java.time.Period;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanServiceInterface {

    public LoanResponse calculateLoan(LoanRequest loanRequest) {
        return  LoanResponse.builder()
                .loanValue(loanRequest.getLoanValue())
                .initialDate(loanRequest.getInitialDate())
                .endDate(loanRequest.getEndDate())
                .firstPaymentDate(loanRequest.getFirstPaymentDate())
                .installmentList(LoanUtils.calculateLoanSAC(loanRequest))
                .build();
    }
}
