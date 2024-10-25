package dev.aries.iijra.utility;

import dev.aries.iijra.constant.ExceptionConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Checks {

	public static <E extends Enum<E>> void checkIfEnumExists(Class<E> enumClass, String request) {
		try {
			Enum.valueOf(enumClass, request.toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ExceptionConstant.INVALID_ENUM_VALUE);
		}
	}
}
