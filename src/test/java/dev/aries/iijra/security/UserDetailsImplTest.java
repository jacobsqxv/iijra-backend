package dev.aries.iijra.security;

import java.util.Collection;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.module.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {
	private User user;
	private UserDetailsImpl userDetails;

	@BeforeEach
	void setUp() {
		user = TestDataFactory.newUser();
		userDetails = new UserDetailsImpl(user);
	}

	@Test
	@DisplayName("Should return correct number of roles when getAuthorities()")
	void getAuthorities_ShouldReturnCorrectRole() {
		SimpleGrantedAuthority expectedAuthority = new SimpleGrantedAuthority(Role.STAFF.name());

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertTrue(authorities.contains(expectedAuthority));
	}

	@Test
	@DisplayName("Should return same role in getAuthorities()")
	void getAuthorities_WithAdminRole_ShouldReturnAdminAuthority() {
		user.setRole(Role.SYS_ADMIN);
		SimpleGrantedAuthority expectedAuthority = new SimpleGrantedAuthority(Role.SYS_ADMIN.name());

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertTrue(authorities.contains(expectedAuthority));
	}

	@Test
	@DisplayName("Should return hashed password")
	void getPassword_ShouldReturnStaffPassword() {
		String password = userDetails.getPassword();

		assertEquals("hashedPassword", password);
	}

	@Test
	@DisplayName("Should return email when getUsername()")
	void getUsername_ShouldReturnStaffEmail() {
		String username = userDetails.getUsername();

		assertEquals("test@email.com", username);
	}

	@Test
	@DisplayName("Should return true when isArchived is set to true")
	void isEnabled_WhenIsActive_ShouldReturnTrue() {
		user.setIsArchived(false);

		assertTrue(userDetails.isEnabled());
	}

	@Test
	@DisplayName("Should return false when isArchived is set to false")
	void isEnabled_WhenNotIsActive_ShouldReturnFalse() {
		user.setIsArchived(true);

		assertFalse(userDetails.isEnabled());
	}

	@Test
	void defaultMethods_ShouldReturnExpectedValues() {
		// These are the default implementations from UserDetails interface
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
	}
}
