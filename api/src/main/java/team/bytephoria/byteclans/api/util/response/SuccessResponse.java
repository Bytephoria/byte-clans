package team.bytephoria.byteclans.api.util.response;

import java.util.Objects;

record SuccessResponse<R>(R result) implements Response<R> {

    public SuccessResponse {
        Objects.requireNonNull(result);
    }

    @Override
    public boolean success() {
        return true;
    }
}
