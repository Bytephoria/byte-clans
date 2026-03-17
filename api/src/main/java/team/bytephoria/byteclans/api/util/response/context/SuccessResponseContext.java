package team.bytephoria.byteclans.api.util.response.context;

import java.util.Objects;

record SuccessResponseContext<T, R>(T value, R result) implements ResponseContext<T, R> {

    SuccessResponseContext {
        Objects.requireNonNull(value, "value cannot be null");
        Objects.requireNonNull(result, "result cannot be null");
    }

    @Override
    public boolean success() {
        return true;
    }
}
