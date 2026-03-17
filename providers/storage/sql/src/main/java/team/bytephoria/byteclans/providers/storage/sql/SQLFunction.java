package team.bytephoria.byteclans.providers.storage.sql;

@FunctionalInterface
public interface SQLFunction<T, R> {

    R apply(T t) throws Exception;

}