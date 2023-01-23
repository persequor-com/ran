/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

public class InjectImpl implements Inject {
	@Override
	public Class<? extends Annotation> annotationType() {
		return Inject.class;
	}
}
