package team.bytephoria.byteclans.api.util;

import org.jetbrains.annotations.NotNull;

public final class IntValue {

    private int value;

    public IntValue(int value) {
        this.value = value;
    }

    public IntValue() {
        this(0);
    }

    public int value() {
        return this.value;
    }

    public void value(int value) {
        this.value = value;
    }

    public void value(final int value, final @NotNull Operation operation) {
        this.value = operation.resolve(this.value, value);
    }

    public void add(final int value) {
        this.value += value;
    }

    public void remove(final int value) {
        this.value -= value;
    }

    public void increase() {
        this.value++;
    }

    public void decrease() {
        this.value--;
    }

    public int getAndIncrease() {
        return this.value++;
    }

    public int getAndDecrease() {
        return this.value--;
    }

    public int increaseAndGet() {
        return ++this.value;
    }

    public int decreaseAndGet() {
        return --this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
