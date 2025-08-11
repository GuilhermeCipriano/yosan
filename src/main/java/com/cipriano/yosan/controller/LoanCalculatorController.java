package com.cipriano.yosan.controller;

import com.cipriano.yosan.dto.request.LoanRequest;
import com.cipriano.yosan.dto.response.LoanResponse;
import com.cipriano.yosan.service.LoanServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/yosan")
public class LoanCalculatorController {

    LoanServiceImpl loanServiceImpl;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanCalculatorController.class);

    LoanCalculatorController(LoanServiceImpl loanServiceImpl) {
        this.loanServiceImpl = loanServiceImpl;
    }

    @PostMapping("/calculate-sac-loan")
    ResponseEntity<LoanResponse> calculateSACLoan(@RequestBody LoanRequest loanRequest) throws InterruptedException {
        LOGGER.info("method::calculateSACLoan called");
        try {
            Thread.sleep(1000);
            LoanResponse responseBody = this.loanServiceImpl.calculateLoan(loanRequest);
            LOGGER.info("method::calculateSACLoan succeeded");
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data, plase chack: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while calculating loan: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch(Exception e) {
            LOGGER.warn("Unknown error: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
