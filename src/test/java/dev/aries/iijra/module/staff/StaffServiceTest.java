package dev.aries.iijra.module.staff;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.department.DepartmentService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import dev.aries.iijra.search.GetStaffPage;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {
	private static final String TEST_USER_EMAIL = "test@email.com";
	private static final Long TEST_DEPT_ID = 1L;
	@InjectMocks
	private StaffService staffService;
	@Mock
	private UserService userService;
	@Mock
	private DepartmentService departmentService;
	@Mock
	private StaffRepository staffRepo;

	private User testUser;
	private Department testDept;

	@Nested
	@DisplayName("Add staff tests")
	class AddStaffTests {
		private StaffRequest testRequest;

		@BeforeEach
		void setUp() {
			testDept = TestDataFactory.newDepartment();
			testUser = TestDataFactory.newUser();
		}

		@Test
		@DisplayName("Should successfully add new staff member if request is valid and department exists")
		void addStaff_WithValidName_Success() {
			testRequest = new StaffRequest(TEST_USER_EMAIL, "John Doe", TEST_DEPT_ID, false);
			doReturn(testUser).when(userService).createUser(TEST_USER_EMAIL, Role.STAFF);
			doReturn(testDept).when(departmentService).getDepartmentById(TEST_DEPT_ID);

			StaffResponse response = staffService.addNewStaff(testRequest);

			ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
			verify(staffRepo).save(staffCaptor.capture());
			Staff savedStaff = staffCaptor.getValue();

			assertEquals(savedStaff.getId(), response.staffId());
			assertFalse(savedStaff.getIsHod());
			verify(staffRepo, times(1)).save(any(Staff.class));
		}

		@Test
		@DisplayName("Should successfully set HOD if valid request and department has no HOD")
		void addStaff_NoHODForDepartment_SuccessTest() {
			testRequest = new StaffRequest(TEST_USER_EMAIL, "John Doe", TEST_DEPT_ID, true);
			doReturn(testUser).when(userService).createUser(TEST_USER_EMAIL, Role.STAFF);
			doReturn(testDept).when(departmentService).getDepartmentById(TEST_DEPT_ID);
			doNothing().when(departmentService).assignHod(any(Staff.class), anyBoolean());

			StaffResponse response = staffService.addNewStaff(testRequest);

			ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
			verify(staffRepo).save(staffCaptor.capture());
			Staff savedStaff = staffCaptor.getValue();

			assertEquals(savedStaff.getId(), response.staffId());
			verify(staffRepo, times(1)).save(any(Staff.class));
			verify(departmentService).assignHod(any(Staff.class), eq(false));
		}

		@Test
		@DisplayName("Should throw exception if valid request and department already has HOD")
		void createStaff_WhenDepartmentHasHodAndIsHodTrue_ShouldThrowException() {
			testRequest = new StaffRequest(TEST_USER_EMAIL, "John Doe", TEST_DEPT_ID, true);
			Staff existingHod = TestDataFactory.newStaff();
			existingHod.setFullName("Existing HOD");
			testDept.setHod(existingHod);

			when(departmentService.getDepartmentById(1L)).thenReturn(testDept);
			doReturn(testUser).when(userService).createUser(TEST_USER_EMAIL, Role.STAFF);
			doThrow(new IllegalStateException(
					String.format("Department %s already has HOD: %s.",
							testDept.getName(),
							existingHod.getFullName())
			)).when(departmentService).assignHod(any(Staff.class), anyBoolean());

			assertThrows(IllegalStateException.class, () -> staffService.addNewStaff(testRequest));

			verify(staffRepo).save(any(Staff.class));
			verify(departmentService).assignHod(any(Staff.class), eq(false));
		}
	}

	@Nested
	@DisplayName("Get staff tests")
	class GetStaffTests {
		private GetStaffPage getPageRequest;
		private Staff testStaff1;
		private Staff testStaff2;
		private Pageable pageable;

		@BeforeEach
		void setUp() {
			testStaff1 = TestDataFactory.newStaff();
			testStaff2 = TestDataFactory.newStaff();
			testStaff2.setId("ST002");
			pageable = PageRequest.of(0,10);
		}

		@Test
		@DisplayName("Should return correct number of all staff")
		void getAllStaff_ShouldReturnPage_Success() {
			getPageRequest = GetStaffPage.builder().build();
			Page<Staff> staffPage = new PageImpl<>(List.of(testStaff1, testStaff2));
			when(staffRepo.findAll(getPageRequest, pageable)).thenReturn(staffPage);
			Page<StaffResponse> response = staffService.getAllStaff(getPageRequest, pageable);

			assertNotNull(response);
			assertEquals(staffPage.getTotalElements(), response.getTotalElements());
		}

		@Test
		@DisplayName("Should throw exception when invalid status is passed")
		void getAllStaff_ShouldThrowException_InvalidStatus() {
			getPageRequest = GetStaffPage.builder().status("INVALID_STATUS").build();


			IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
					() -> staffService.getAllStaff(getPageRequest, pageable));

			assertNotNull(ex);
			assertEquals(ExceptionConstant.INVALID_ENUM_VALUE, ex.getMessage());
		}

		@Test
		@DisplayName("Should return staff when id is valid")
		void getStaff_WithValidId_Success() {
			Long staffId = 1L;
			when(staffRepo.findById(any(String.class))).thenReturn(Optional.of(testStaff1));

			StaffResponse response = staffService.getStaffById(staffId);

			verify(staffRepo, times(1)).findById(any(String.class));
			assertNotNull(response);
			assertTrue(response.staffId().contains("ST"));
		}

		@Test
		@DisplayName("Should throw exception when staff does not exist")
		void getStaff_WithInvalidId_ThrowException() {
			Long staffId = 1L;
			when(staffRepo.findById(any(String.class))).thenReturn(Optional.empty());

			EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
					() -> staffService.getStaffById(staffId));

			verify(staffRepo, times(1)).findById(any(String.class));
			assertNotNull(ex);
			assertTrue(ex.getMessage().contains(ExceptionConstant.STAFF_ID_DOESNT_EXIST));
		}
	}
}
