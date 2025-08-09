package com.cipriano.yosan.service.interfaces;

import com.cipriano.yosan.dto.request.LoanRequest;
import com.cipriano.yosan.dto.response.LoanResponse;

public interface LoanServiceInterface {

    public LoanResponse calculateLoan(LoanRequest loanRequest);
}
