package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoanTool {

    @Tool(name = "open_loan_contract", description = "It opens a DRAFT loan contract and returns the generated loan id")
    public UUID openLoan(@ToolParam(value = "the currency of the new Loan") String currency,
                         @ToolParam(value = "the amount of the new Loan") String amount) {
        UUID loanId = UUID.randomUUID();
        System.out.println("Open loan: " + loanId + " " + amount + " " + currency);
        return loanId;
    }

    @Tool(name = "activate_loan_contract", description = "It activates the DRAFT loan contract for the given loan id")
    void activateLoan(UUID loanId) {
        System.out.println("Activate loan: " + loanId);
    }

    @Tool(name = "terminate_loan_contract", description = "It terminates the loan contract for the given loan id")
    void terminateLoan(UUID loanId) {
        System.out.println("Terminate loan: " + loanId);
    }
}
