package eu.hexgate.blog.externalmodules;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TaxService {
    public BigDecimal gerCurrentTax() {
        return new BigDecimal("0.2");
    }
}
