package ma.atos.billing.invoice.billing_invoice.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("invoiceControlDelegate")
public class InvoiceControlDelegate implements JavaDelegate {

    private static final BigDecimal LIMIT = new BigDecimal("10000");

    @Override
    public void execute(DelegateExecution execution) {
        String invoiceNumber = (String) execution.getVariable("invoiceNumber");
        BigDecimal amount = new BigDecimal(execution.getVariable("amount").toString());

        boolean needsManagerApproval = amount.compareTo(LIMIT) > 0;

        execution.setVariable("needsManagerApproval", needsManagerApproval);

        System.out.println("Invoice controlled: " + invoiceNumber);
        System.out.println("Needs manager approval: " + needsManagerApproval);
    }
}