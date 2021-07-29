package eu.hexgate.blog.order.dto;

public class ErrorDto {

    private final String message;
    private final String resourceId;

    public ErrorDto(String message, String resourceId) {
        this.message = message;
        this.resourceId = resourceId;
    }

    public String getMessage() {
        return message;
    }

    public String getResourceId() {
        return resourceId;
    }
}
