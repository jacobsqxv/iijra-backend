package dev.aries.iijra.utility;

import java.util.function.Consumer;

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

	public static <T> void updateField(Consumer<T> setter, T currVal, T newVal) {
		if (newVal == null || newVal.equals(currVal)) {
			return;
		}

		setter.accept(newVal);
	}

	private Checks() {}
}
