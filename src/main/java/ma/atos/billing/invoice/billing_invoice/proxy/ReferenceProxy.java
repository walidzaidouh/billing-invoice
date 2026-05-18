package ma.atos.billing.invoice.billing_invoice.proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${app.feign-client.reference-service.name}", url = "${app.feign-client.reference-service.url}")
public interface ReferenceProxy {

    @GetMapping("${app.feign-client.reference-service.path}")
    String getReference(@PathVariable String reference);
}
