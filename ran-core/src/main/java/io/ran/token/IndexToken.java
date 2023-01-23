/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.token;

import io.ran.Property;

public abstract class IndexToken extends FormattingToken {
	protected IndexToken(Property property) {
		super(property);
	}

	protected IndexToken(Token token) {
		super(token);
	}

	protected IndexToken(String concreteName) {
		super(concreteName);
	}
}
