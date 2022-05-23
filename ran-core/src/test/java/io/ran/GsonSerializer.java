package io.ran;

import com.google.gson.Gson;

public class GsonSerializer implements ISerializer {
    Gson gson = new Gson();

    @Override
    public <T> T deserialize(Class<T> clazz, String value) {
        return gson.fromJson(value, clazz);
    }

    @Override
    public <T> String serialize(T object) {
        return gson.toJson(object);
    }
}
