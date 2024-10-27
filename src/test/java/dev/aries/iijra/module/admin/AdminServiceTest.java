package dev.aries.iijra.module.admin;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
	private static final String TEST_USER_EMAIL = "test@email.com";
	private static final String TEST_USER_NAME = "John Doe";

	@InjectMocks
	private AdminService adminService;
	@Mock
	private UserService userService;
	@Mock
	private AdminRepository adminRepo;

	private User testUser;

	private AdminRequest testRequest;

	@Nested
	@DisplayName("Add new admin tests")
	class AddAdminTests {
		@BeforeEach
		void setUp() {
			testUser = TestDataFactory.newUser();
			testRequest = new AdminRequest(TEST_USER_EMAIL, TEST_USER_NAME);
		}

		@Test
		@DisplayName("Should successfully add new admin with valid request")
		void addAdmin_WithValidRequest_SuccessTest() {
			doReturn(testUser).when(userService).createUser(TEST_USER_EMAIL, Role.SYS_ADMIN);

			AdminResponse response = adminService.addNewSystemAdmin(testRequest);

			ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
			verify(adminRepo).save(adminCaptor.capture());
			Admin savedAdmin = adminCaptor.getValue();

			assertEquals(savedAdmin.getId(), response.id());
			assertEquals(TEST_USER_EMAIL, response.email());
			assertEquals(TEST_USER_NAME, response.fullName());
		}
	}
}
