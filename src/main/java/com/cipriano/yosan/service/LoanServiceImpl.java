package com.cipriano.yosan.service;

import com.cipriano.yosan.LoanUtils;
import com.cipriano.yosan.dto.request.LoanRequest;
import com.cipriano.yosan.dto.response.LoanResponse;
import com.cipriano.yosan.service.interfaces.LoanServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanServiceInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanUtils.class);

    public LoanResponse calculateLoan(LoanRequest loanRequest) {
        LOGGER.info("method::calculateLoan called");
        LOGGER.debug("initialDate: {}", loanRequest.getInitialDate());
        LOGGER.debug("In this class is in charge of calling database connection and other businesses' logic class such as LoanUtils");
        LOGGER.debug("This is just an over exaggerating example of logs and the class is here just to create an pattern ");
        return  LoanResponse.builder()
                .loanValue(loanRequest.getLoanValue())
                .taxRate(loanRequest.getTaxRate())
                .initialDate(loanRequest.getInitialDate())
                .endDate(loanRequest.getEndDate())
                .firstPaymentDate(loanRequest.getFirstPaymentDate())
                .installmentList(LoanUtils.calculateLoanSAC(loanRequest))
                .build();
    }
}
