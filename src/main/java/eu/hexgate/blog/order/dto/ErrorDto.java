package eu.hexgate.blog.order.dto;

public class ErrorDto {

    private String message;
    private String resourceId;

    public ErrorDto(String message, String resourceId) {
        this.message = message;
        this.resourceId = resourceId;
    }

    public ErrorDto() {
    }

    public String getMessage() {
        return message;
    }

    public String getResourceId() {
        return resourceId;
    }
}
