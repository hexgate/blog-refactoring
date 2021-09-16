package eu.hexgate.blog.order.web;

import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.domain.errors.DomainErrorCode;
import eu.hexgate.blog.order.dto.ErrorDto;
import io.vavr.Function1;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.springframework.http.ResponseEntity;

public class DomainErrorResolver {

    private static final ResponseEntity<ErrorDto> NOT_FOUND = ResponseEntity
            .notFound()
            .build();

    private static final Map<DomainErrorCode, Function1<DomainError, ResponseEntity<ErrorDto>>> MAPPING = HashMap.of(
            DomainErrorCode.INVALID_ORDER_STATUS, domainError -> ResponseEntity
                    .badRequest()
                    .body(new ErrorDto(domainError.getMessage().getOrNull(), domainError.getAdditionalData().getOrNull())),
            DomainErrorCode.ORDER_NOT_FOUND, domainError -> NOT_FOUND
    );

    private static final ResponseEntity<ErrorDto> DEFAULT_ERROR = ResponseEntity.badRequest()
            .build();

    ResponseEntity<ErrorDto> resolve(final DomainError domainError) {
        return MAPPING.get(domainError.getCode())
                .map(it -> it.apply(domainError))
                .getOrElse(() -> DEFAULT_ERROR);
    }
}
