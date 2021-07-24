package eu.hexgate.blog.uglyorder.externalmodules;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingService {

    public BigDecimal getCurrentShippingPrice() {
        return new BigDecimal("15");
    }
}
