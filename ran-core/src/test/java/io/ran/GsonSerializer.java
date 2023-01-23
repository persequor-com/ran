/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-04-01
 */
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
