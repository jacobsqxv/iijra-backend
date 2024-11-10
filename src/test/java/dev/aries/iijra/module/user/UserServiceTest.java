package dev.aries.iijra.module.user;

import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	private static final String TEST_USER_EMAIL = "test@example.com";
	private static final Long TEST_USER_ID = 1L;

	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepo;
	@Mock
	private PasswordEncoder passwordEncoder;

	private User testUser;

	@Nested
	@DisplayName("Create user tests")
	class CreateUserTests {
		@Test
		@DisplayName("Should create new user successfully given valid request")
		void createUser_WithValidRequest_SuccessTest() {
			Role userRole = Role.STAFF;

			userService.createUser(TEST_USER_EMAIL, userRole);
			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepo).save(userCaptor.capture());

			User savedUser = userCaptor.getValue();
			assertEquals(userRole, savedUser.getRole());
			assertEquals(TEST_USER_EMAIL, savedUser.getEmail());
		}

		@Test
		@DisplayName("Should throw exception when email already exists")
		void createUser_WithExistingEmail_ShouldThrowException() {
			Role userRole = Role.STAFF;

			when(userRepo.existsByEmail(TEST_USER_EMAIL)).thenReturn(true);
			EntityExistsException ex = assertThrows(EntityExistsException.class,
					() -> userService.createUser(TEST_USER_EMAIL, userRole));
			assertTrue(ex.getMessage().contains(ExceptionConstant.USER_EMAIL_ALREADY_EXISTS));
			assertTrue(ex.getMessage().contains(TEST_USER_EMAIL));
		}
	}

	@Nested
	@DisplayName("Update password tests")
	class UpdatePasswordTests {
		private PasswordUpdateRequest request;

		@BeforeEach
		void setUp() {
			request = new PasswordUpdateRequest("currentPass", "newPass123");
		}

		@Test
		@DisplayName("Should update password when current password matches and newPassword is valid")
		void updatePassword_WithValidCurrentPass_SuccessTest() {
			testUser = TestDataFactory.newUser();

			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
			when(passwordEncoder.matches(any(), any())).thenReturn(true);

			String response = userService.updatePassword(TEST_USER_ID, request);

			assertEquals("Password updated successfully", response);
			verify(userRepo, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("Should throw exception when user does not exist")
		void updatePassword_WithNonExistentUser_ShouldThrowException() {
			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.empty());

			EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
					() -> userService.updatePassword(TEST_USER_ID, request));
			assertTrue(ex.getMessage().contains(ExceptionConstant.USER_ID_DOESNT_EXIST));
			assertTrue(ex.getMessage().contains(TEST_USER_ID.toString()));
		}

		@Test
		@DisplayName("Should throw exception when current password does not match")
		void updatePassword_WithInvalidCurrentPassword_ShouldThrowException() {
			testUser = TestDataFactory.newUser();
			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
			when(passwordEncoder.matches(any(), any())).thenReturn(false);

			IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
					() -> userService.updatePassword(TEST_USER_ID, request));
			assertTrue(ex.getMessage().contains(ExceptionConstant.INVALID_CURRENT_PASSWORD));
		}
	}

	@Nested
	@DisplayName("Archive user tests")
	class ArchiveUserTests {
		@BeforeEach
		void setUp() {
			testUser = TestDataFactory.newUser();
		}

		@Test
		@DisplayName("Should successfully archive user when not already archived")
		void archiveUser_WithValidUserId_SuccessTest() {
			testUser.setArchived(false);

			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

			String response = userService.archiveUser(TEST_USER_ID);

			assertTrue(response.contains(testUser.getEmail()));
			verify(userRepo, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("Should throw exception when user is already archived")
		void archiveUser_WithAlreadyArchivedUser_ShouldThrowException() {
			testUser.setArchived(true);

			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> userService.archiveUser(TEST_USER_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.USER_ALREADY_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_USER_ID.toString()));
		}
	}

	@Nested
	@DisplayName("Restore archived user tests")
	class RestoreUserTests {
		@BeforeEach
		void setUp() {
			testUser = TestDataFactory.newUser();
		}

		@Test
		@DisplayName("Should successfully restore archived user when already archived")
		void archiveUser_WithValidUserId_SuccessTest() {
			testUser.setArchived(true);

			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

			String response = userService.restoreArchivedUser(TEST_USER_ID);

			assertTrue(response.contains(testUser.getEmail()));
			verify(userRepo, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("Should throw exception when user is not archived")
		void archiveUser_WithAlreadyArchivedUser_ShouldThrowException() {
			testUser.setArchived(false);

			when(userRepo.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> userService.restoreArchivedUser(TEST_USER_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.USER_NOT_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_USER_ID.toString()));
		}
	}
}
