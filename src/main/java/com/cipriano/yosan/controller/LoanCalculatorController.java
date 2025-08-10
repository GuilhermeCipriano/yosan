package com.cipriano.yosan.controller;

import com.cipriano.yosan.dto.request.LoanRequest;
import com.cipriano.yosan.dto.response.LoanResponse;
import com.cipriano.yosan.service.LoanServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/yosan")
public class LoanCalculatorController {

    LoanCalculatorController() {
        this.loanServiceImpl = new LoanServiceImpl();
    }

    LoanServiceImpl loanServiceImpl;


    @PostMapping("/calculate-sac-loan")
    LoanResponse calculateSACLoan(@RequestBody LoanRequest loanRequest) throws InterruptedException {
        Thread.sleep(1000);
        return this.loanServiceImpl.calculateLoan(loanRequest);
    }

}
