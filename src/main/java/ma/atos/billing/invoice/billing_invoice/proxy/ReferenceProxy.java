package ma.atos.billing.invoice.billing_invoice.proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reference-service", url = "http://localhost:8080")
public interface ReferenceProxy {

    @GetMapping("/api/reference/{reference}")
    String getReference(@PathVariable String reference);
}
