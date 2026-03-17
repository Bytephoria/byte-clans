package team.bytephoria.byteclans.api.util.response;

import java.util.Objects;

record FailureResponse<R>(R result) implements Response<R> {

    public FailureResponse {
        Objects.requireNonNull(result);
    }

    @Override
    public boolean success() {
        return false;
    }
}
