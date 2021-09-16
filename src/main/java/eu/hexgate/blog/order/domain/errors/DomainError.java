package eu.hexgate.blog.order.domain.errors;

import io.vavr.control.Option;

public class DomainError {

    private final DomainErrorCode code;
    private final String message;
    private final String additionalData;

    private DomainError(DomainErrorCode code, String message, String additionalData) {
        this.code = code;
        this.message = message;
        this.additionalData = additionalData;
    }

    public static DomainError orderNotFound(String orderId) {
        return DomainError.withCode(DomainErrorCode.ORDER_NOT_FOUND)
                .withAdditionalData(orderId)
                .withMessage("Order not found.")
                .build();
    }

    public static Builder withCode(DomainErrorCode domainErrorCode) {
        return new Builder(domainErrorCode);
    }

    public DomainErrorCode getCode() {
        return code;
    }

    public Option<String> getMessage() {
        return Option.of(message);
    }

    public Option<String> getAdditionalData() {
        return Option.of(additionalData);
    }

    public static class Builder {
        private final DomainErrorCode code;
        private String message;
        private String additionalData;

        private Builder(DomainErrorCode code) {
            this.code = code;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withAdditionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public DomainError build() {
            return new DomainError(code, message, additionalData);
        }
    }
}
