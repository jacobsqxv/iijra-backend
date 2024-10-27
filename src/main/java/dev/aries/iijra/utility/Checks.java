package dev.aries.iijra.utility;

import dev.aries.iijra.constant.ExceptionConstant;

public final class Checks {

	public static <E extends Enum<E>> void checkIfEnumExists(Class<E> enumClass, String request) {
		try {
			Enum.valueOf(enumClass, request.toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ExceptionConstant.INVALID_ENUM_VALUE);
		}
	}

	private Checks() {}
}
