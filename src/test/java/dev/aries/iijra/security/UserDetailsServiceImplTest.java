package dev.aries.iijra.security;

import java.util.Optional;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	private static final String TEST_EMAIL = "test@example.com";
	@Mock
	private StaffRepository staffRepo;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;
	private Staff testStaff;

	@BeforeEach
	void setUp() {
		testStaff = new Staff();
		testStaff.setEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
		when(staffRepo.findByEmail(TEST_EMAIL))
				.thenReturn(Optional.of(testStaff));

		UserDetails result = userDetailsService.loadUserByUsername(TEST_EMAIL);

		assertNotNull(result);
		assertInstanceOf(UserDetailsImpl.class, result);
		assertEquals(TEST_EMAIL, result.getUsername());

		verify(staffRepo, times(1)).findByEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenUserDoesNotExist_ThrowsException() {
		when(staffRepo.findByEmail(TEST_EMAIL))
				.thenReturn(Optional.empty());

		Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
			userDetailsService.loadUserByUsername(TEST_EMAIL);
		});

		assertEquals("Staff does not exist", exception.getMessage());

		verify(staffRepo, times(1)).findByEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenEmailIsNull_ThrowsException() {
		assertThrows(NullPointerException.class, () -> {
			userDetailsService.loadUserByUsername(null);
		});

		verify(staffRepo, never()).findByEmail(any());
	}
}
