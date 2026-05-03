package team.bytephoria.byteclans.api.util;

public enum Operation {

    SUM,
    SUB,
    MUL,
    DIV,
    MOD;

    public int resolve(final int value, final int other) {
        return switch (this) {
            case SUM -> value + other;
            case SUB -> value - other;
            case MUL -> value * other;
            case DIV -> value / other;
            case MOD -> value % other;
        };
    }

}