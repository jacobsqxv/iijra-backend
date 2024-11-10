package dev.aries.iijra.module.staff;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.exception.S3UploadException;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.department.DepartmentService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import dev.aries.iijra.search.GetStaffPage;
import dev.aries.iijra.utility.S3Service;
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
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {
	private static final String TEST_USER_EMAIL = "test@email.com";
	private static final Long TEST_DEPT_ID = 1L;
	private static final Long STAFF_ID = 2L;
	@InjectMocks
	private StaffService staffService;
	@Mock
	private S3Service s3Service;
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
			testDept.setId(1L);
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
			assertFalse(savedStaff.getHod());
		}

		@Test
		@DisplayName("Should successfully set HOD if valid request and department has no HOD")
		void addStaff_NoHODForDepartment_SuccessTest() {
			testRequest = new StaffRequest(TEST_USER_EMAIL, "John Doe", TEST_DEPT_ID, true);
			doReturn(testUser).when(userService).createUser(TEST_USER_EMAIL, Role.HOD);
			when(departmentService.getDepartmentById(TEST_DEPT_ID)).thenReturn(testDept);
			when(staffRepo.findByDepartmentIdAndHodTrue(TEST_DEPT_ID)).thenReturn(Optional.empty());

			StaffResponse response = staffService.addNewStaff(testRequest);

			ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
			verify(staffRepo).save(staffCaptor.capture());
			Staff savedStaff = staffCaptor.getValue();

			assertEquals(savedStaff.getId(), response.staffId());
			verify(staffRepo, times(1)).save(any(Staff.class));
		}

		@Test
		@DisplayName("Should throw exception if valid request and department already has HOD")
		void createStaff_WhenDepartmentHasHodAndIsHodTrue_ShouldThrowException() {
			testRequest = new StaffRequest(TEST_USER_EMAIL, "John Doe", TEST_DEPT_ID, true);
			Staff existingHod = TestDataFactory.newStaff();
			existingHod.setFullName("Existing HOD");
			when(staffRepo.findByDepartmentIdAndHodTrue(TEST_DEPT_ID)).thenReturn(Optional.of(existingHod));

			when(departmentService.getDepartmentById(1L)).thenReturn(testDept);

			assertThrows(IllegalStateException.class, () -> staffService.addNewStaff(testRequest));

			verify(staffRepo, never()).save(any(Staff.class));
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
			pageable = PageRequest.of(0, 10);
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

			IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> staffService.getAllStaff(getPageRequest, pageable));

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

			EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> staffService.getStaffById(staffId));

			verify(staffRepo, times(1)).findById(any(String.class));
			assertNotNull(ex);
			assertTrue(ex.getMessage().contains(ExceptionConstant.STAFF_ID_DOESNT_EXIST));
		}
	}

	@Nested
	@DisplayName("Update staff tests")
	class UpdateStaffTests {
		private static final String FORMATTED_STAFF_ID = "ST0002";
		private static final String DEFAULT_PROFILE_IMAGE = "default.jpg";
		private StaffUpdateRequest updateRequest;
		private Staff existingStaff;
		private MultipartFile mockProfileImage;

		@BeforeEach
		void setup() {
			existingStaff = TestDataFactory.newStaff();
			existingStaff.setId(FORMATTED_STAFF_ID);
			existingStaff.setFullName("John Doe");
			existingStaff.setBio("Original bio");
			existingStaff.setProfileImage("original-image.jpg");

			// Mock profile image
			mockProfileImage = mock(MultipartFile.class);
		}

		@Test
		@DisplayName("Should successfully update staff if only text fields are present")
		void updateStaffInfo_OnlyTextFields_Success() {
			// Arrange
			updateRequest = new StaffUpdateRequest(null, "Jane Doe", "Updated bio");

			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.of(existingStaff));
			when(staffRepo.save(any(Staff.class))).thenAnswer(i -> i.getArguments()[0]);

			// Act
			StaffResponse response = staffService.updateStaffInfo(STAFF_ID, updateRequest);

			// Assert
			assertThat(response.fullName()).isEqualTo("Jane Doe");
			assertThat(response.bio()).isEqualTo("Updated bio");
			assertThat(response.profileImage()).isEqualTo("original-image.jpg");

			verify(s3Service, never()).uploadFile(any(), any());
			verify(s3Service, never()).deleteFile(any());
			verify(staffRepo).save(existingStaff);
		}

		@Test
		@DisplayName("Should successfully update staff if all fields are present")
		void updateStaffInfo_WithNewImage_Success() {
			// Arrange
			String newImageUrl = "new-image.jpg";
			updateRequest = new StaffUpdateRequest(mockProfileImage, "Jane Doe", "Updated bio");

			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.of(existingStaff));
			when(s3Service.uploadFile(mockProfileImage, FORMATTED_STAFF_ID)).thenReturn(newImageUrl);
			when(staffRepo.save(any(Staff.class))).thenAnswer(i -> i.getArguments()[0]);

			// Act
			StaffResponse response = staffService.updateStaffInfo(STAFF_ID, updateRequest);

			// Assert
			assertThat(response.fullName()).isEqualTo("Jane Doe");
			assertThat(response.bio()).isEqualTo("Updated bio");
			assertThat(response.profileImage()).isEqualTo(newImageUrl);

			verify(s3Service).uploadFile(mockProfileImage, FORMATTED_STAFF_ID);
			verify(s3Service).deleteFile("original-image.jpg");
			verify(staffRepo).save(existingStaff);
		}

		@Test
		@DisplayName("Should not delete default image when the staff is updated")
		void updateStaffInfo_WithDefaultImage_DoesNotDeleteOldImage() {
			// Arrange
			existingStaff.setProfileImage(DEFAULT_PROFILE_IMAGE);
			String newImageUrl = "new-image.jpg";
			updateRequest = new StaffUpdateRequest(mockProfileImage, "Jane Doe", "Updated bio");

			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.of(existingStaff));
			when(s3Service.uploadFile(mockProfileImage, FORMATTED_STAFF_ID)).thenReturn(newImageUrl);
			when(staffRepo.save(any(Staff.class))).thenAnswer(i -> i.getArguments()[0]);

			// Act
			StaffResponse response = staffService.updateStaffInfo(STAFF_ID, updateRequest);

			// Assert
			assertThat(response.profileImage()).isEqualTo(newImageUrl);

			verify(s3Service).uploadFile(mockProfileImage, FORMATTED_STAFF_ID);
			verify(staffRepo).save(existingStaff);
		}

		@Test
		@DisplayName("Should keep existing values if request fields are null")
		void updateStaffInfo_NullFields_KeepsExistingValues() {
			// Arrange
			updateRequest = new StaffUpdateRequest(null, null, null);

			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.of(existingStaff));
			when(staffRepo.save(any(Staff.class))).thenAnswer(i -> i.getArguments()[0]);

			// Act
			StaffResponse response = staffService.updateStaffInfo(STAFF_ID, updateRequest);

			// Assert
			assertThat(response.fullName()).isEqualTo(existingStaff.getFullName());
			assertThat(response.bio()).isEqualTo(existingStaff.getBio());
			assertThat(response.profileImage()).isEqualTo(existingStaff.getProfileImage());

			verify(s3Service, never()).uploadFile(any(), any());
			verify(s3Service, never()).deleteFile(any());
			verify(staffRepo).save(existingStaff);
		}

		@Test
		@DisplayName("Should throw exception if staff not found")
		void updateStaffInfo_StaffNotFound_ThrowsException() {
			// Arrange
			updateRequest = new StaffUpdateRequest(null, "New Name", "New Bio");
			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.empty());

			// Act & Assert
			assertThatThrownBy(() -> staffService.updateStaffInfo(STAFF_ID, updateRequest)).isInstanceOf(EntityNotFoundException.class).hasMessage(ExceptionConstant.STAFF_ID_DOESNT_EXIST + STAFF_ID);

			verify(staffRepo, never()).save(any());
			verify(s3Service, never()).uploadFile(any(), any());
			verify(s3Service, never()).deleteFile(any());
		}

		@Test
		@DisplayName("Should abort update and throw exception if S3 upload fails")
		void updateStaffInfo_S3UploadFails_ThrowsException() {
			// Arrange
			updateRequest = new StaffUpdateRequest(mockProfileImage, "Jane Doe", "Updated bio");

			when(staffRepo.findById(FORMATTED_STAFF_ID)).thenReturn(Optional.of(existingStaff));
			when(s3Service.uploadFile(any(), any())).thenThrow(new S3UploadException(null));

			// Act & Assert
			assertThatThrownBy(() -> staffService.updateStaffInfo(STAFF_ID, updateRequest)).isInstanceOf(S3UploadException.class).hasMessage(ExceptionConstant.FILE_UPLOAD_FAILURE);

			verify(staffRepo, never()).save(any());
			verify(s3Service, never()).deleteFile(any());
		}
	}
}
