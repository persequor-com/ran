package io.ran;

public interface ISerializer {
    <T> T deserialize(Class<T> clazz, String value);

    <T> String serialize(T object);
}
