package com.clustertest.transport;

public class HttpResponse<T> {
    private HttpStatus status;
    private T data;

    public HttpResponse(HttpResponseBuilder<T> builder) {
        this.status = builder.status;
        this.data = builder.data;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public static class HttpResponseBuilder<T> {
        private HttpStatus status;
        private T data;

        public HttpResponseBuilder<T> withStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public HttpResponseBuilder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public HttpResponse<T> build() {
            return new HttpResponse<>(this);
        }
    }
}
