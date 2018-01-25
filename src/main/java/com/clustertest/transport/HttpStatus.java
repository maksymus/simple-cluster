package com.clustertest.transport;

public enum HttpStatus {
    OK(200, "OK"),

    MOVED_PERMANENTLY(301, "Moved Permanently"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    private final int code;
    private final String reason;

    HttpStatus(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
    }

    public int getStatusCode() {
        return code;
    }

    public String getReasonPhrase() {
        return toString();
    }

    @Override
    public String toString() {
        return reason;
    }

    public static HttpStatus fromStatusCode(final int statusCode) {
        for (HttpStatus s : HttpStatus.values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return null;
    }
}
