package io.ran;

public class MissingDbTypeException extends RuntimeException {
    public MissingDbTypeException(String s) {
        super(s);
    }
}
