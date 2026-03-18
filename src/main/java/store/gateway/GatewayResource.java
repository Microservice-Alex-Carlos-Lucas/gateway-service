package store.gateway;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@RestController
public class GatewayResource {
    
    @GetMapping("/accounts")
    public String hello() {
        return "Hello from Gateway!";
    }

    @GetMapping("/health-check")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
