package dev.aries.iijra.security;

import java.util.Optional;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserRepository;
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
	private UserRepository userRepo;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;
	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
		when(userRepo.findByEmail(TEST_EMAIL))
				.thenReturn(Optional.of(testUser));

		UserDetails result = userDetailsService.loadUserByUsername(TEST_EMAIL);

		assertNotNull(result);
		assertInstanceOf(UserDetailsImpl.class, result);
		assertEquals(TEST_EMAIL, result.getUsername());

		verify(userRepo, times(1)).findByEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenUserDoesNotExist_ThrowsException() {
		when(userRepo.findByEmail(TEST_EMAIL))
				.thenReturn(Optional.empty());

		Exception exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(TEST_EMAIL));

		assertEquals(ExceptionConstant.USER_EMAIL_DOESNT_EXIST + TEST_EMAIL, exception.getMessage());

		verify(userRepo, times(1)).findByEmail(TEST_EMAIL);
	}

	@Test
	void loadUserByUsername_WhenEmailIsNull_ThrowsException() {
		//noinspection DataFlowIssue
		assertThrows(NullPointerException.class, () -> userDetailsService.loadUserByUsername(null));

		verify(userRepo, never()).findByEmail(any());
	}
}
