package io.ran;

public interface AutoWrappedFactory {
    <T> T get(String identifier);
}
