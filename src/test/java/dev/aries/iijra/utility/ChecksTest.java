package dev.aries.iijra.utility;

import java.util.function.Consumer;

import dev.aries.iijra.constant.ExceptionConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ChecksTest {

	@Test
	@DisplayName("Should successfully pass check if enum value exists")
	void checkIfEnumExists_shouldPassWhenEnumValueExists() {
		assertDoesNotThrow(() -> Checks.checkIfEnumExists(TestEnum.class, "VALUE1"));
	}

	@Test
	@DisplayName("Should throw exception when enum value does not exist")
	void checkIfEnumExists_shouldThrowExceptionWhenEnumValueDoesNotExist() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> Checks.checkIfEnumExists(TestEnum.class, "INVALID_VALUE"));
		assertEquals(ExceptionConstant.INVALID_ENUM_VALUE, exception.getMessage());
	}

	@Test
	@DisplayName("Should call setter with new value if different from old value")
	void updateField_shouldCallSetter() {
		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);
		Checks.updateField(setter, "oldValue", "newValue");
		verify(setter).accept("newValue");
	}

	@Test
	@DisplayName("Should not call setter when new value is null")
	void updateField_shouldNotCallSetter_whenNewValueIsNull() {
		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);
		Checks.updateField(setter, "oldValue", null);
		verify(setter, never()).accept(any());
	}

	@Test
	@DisplayName("Should not call setter when new value is same as current value")
	void updateField_shouldNotCallSetter_whenNewValueIsEqualToCurrent() {
		@SuppressWarnings("unchecked")
		Consumer<String> setter = mock(Consumer.class);
		Checks.updateField(setter, "value", "value");
		verify(setter, never()).accept(any());
	}

	private enum TestEnum {
		VALUE1, VALUE2, VALUE3
	}
}
