package ma.atos.billing.invoice.billing_invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BillingInvoiceApplication {

	public static void main(String[] args) {
		                                                                SpringApplication.run(BillingInvoiceApplication.class, args);
	}

}
