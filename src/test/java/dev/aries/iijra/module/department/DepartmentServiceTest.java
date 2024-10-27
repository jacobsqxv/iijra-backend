package dev.aries.iijra.module.department;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
	private static final String TEST_DEPT_NAME = "Department";
	private static final Long TEST_DEPT_ID = 1L;
	@InjectMocks
	private DepartmentService departmentService;
	@Mock
	private DepartmentRepository departmentRepo;

	private Department testDepartment;

	@Nested
	@DisplayName("Add department tests")
	class AddDepartmentTests {
		private DepartmentRequest testRequest;

		@BeforeEach
		void setUp() {
			testRequest = new DepartmentRequest(TEST_DEPT_NAME);
		}

		@Test
		@DisplayName("Should successfully add new department if name does not exist")
		void addDepartment_WithValidName_Success() {
			when(departmentRepo.existsByName(TEST_DEPT_NAME)).thenReturn(false);

			DepartmentResponse response = departmentService.addNewDepartment(testRequest);

			ArgumentCaptor<Department> deptCaptor = ArgumentCaptor.forClass(Department.class);
			verify(departmentRepo).save(deptCaptor.capture());
			Department savedDept = deptCaptor.getValue();

			assertEquals(savedDept.getId(), response.id());
			assertEquals(savedDept.getName(), response.name());
			assertEquals(savedDept.getStaff().size(), response.staff());
		}

		@Test
		@DisplayName("Should throw exception when department name already exist")
		void addDepartment_WithAlreadyExistingName_ShouldThrowException() {
			when(departmentRepo.existsByName(TEST_DEPT_NAME)).thenReturn(true);

			EntityExistsException ex = assertThrows(EntityExistsException.class,
					() -> departmentService.addNewDepartment(testRequest));

			assertTrue(ex.getMessage().contains(ExceptionConstant.DEPT_NAME_ALREADY_EXISTS));
			verify(departmentRepo, never()).save(any(Department.class));
		}
	}

	@Nested
	@DisplayName("Get department(s) tests")
	class GetDepartmentTests {
		@Test
		@DisplayName("Should successfully return department if ID exists")
		void getDepartment_WithExistingId_Success() {
			testDepartment = TestDataFactory.newDepartment();
			testDepartment.setId(TEST_DEPT_ID);

			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.of(testDepartment));

			DepartmentResponse response = departmentService.getDepartment(TEST_DEPT_ID);

			assertEquals(TEST_DEPT_ID, response.id());
			verify(departmentRepo, times(1)).findById(TEST_DEPT_ID);
		}

		@Test
		@DisplayName("Should throw exception if id does not exist")
		void getDepartment_WithNonExistingId_ShouldThrowException() {
			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.empty());

			EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
					() -> departmentService.getDepartmentById(TEST_DEPT_ID));

			assertTrue(ex.getMessage().contains(ExceptionConstant.DEPT_ID_DOESNT_EXIST));
		}

		@Test
		@DisplayName("Should successfully return list of departments")
		void getDepartments_Success() {
			Department testDept1 = TestDataFactory.newDepartment();
			testDept1.setId(1L);
			Department testDept2 = TestDataFactory.newDepartment();
			testDept1.setId(2L);

			List<Department> testDepartments = List.of(testDept1, testDept2);

			when(departmentRepo.findByIsArchivedFalse()).thenReturn(testDepartments);

			List<DepartmentResponse> response = departmentService.getAllDepartments();

			verify(departmentRepo).findByIsArchivedFalse();
			assertEquals(testDepartments.size(), response.size());
		}
	}

	@Nested
	@DisplayName("Archive department tests")
	class ArchiveDepartmentTests {
		@BeforeEach
		void setUp() {
			testDepartment = TestDataFactory.newDepartment();
		}

		@Test
		@DisplayName("Should successfully archive department when not already archived")
		void archiveDepartment_WithValidDeptId_SuccessTest() {
			testDepartment.setIsArchived(false);

			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.of(testDepartment));

			String response = departmentService.archiveDepartment(TEST_DEPT_ID);

			assertTrue(response.contains(testDepartment.getName()));
			verify(departmentRepo, times(1)).save(any(Department.class));
		}

		@Test
		@DisplayName("Should throw exception when department is already archived")
		void archiveDepartment_WithAlreadyArchivedDept_ShouldThrowException() {
			testDepartment.setIsArchived(true);

			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.of(testDepartment));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> departmentService.archiveDepartment(TEST_DEPT_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.DEPT_ALREADY_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_DEPT_ID.toString()));
		}
	}

	@Nested
	@DisplayName("Restore archived department tests")
	class RestoreDepartmentTests {
		@BeforeEach
		void setUp() {
			testDepartment = TestDataFactory.newDepartment();
		}

		@Test
		@DisplayName("Should successfully restore archived department when already archived")
		void restoreArchivedDepartment_WithValidDeptId_SuccessTest() {
			testDepartment.setIsArchived(true);

			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.of(testDepartment));

			String response = departmentService.restoreArchivedDepartment(TEST_DEPT_ID);

			assertTrue(response.contains(testDepartment.getName()));
			verify(departmentRepo, times(1)).save(any(Department.class));
		}

		@Test
		@DisplayName("Should throw exception when department is not archived")
		void restoreArchivedDepartment_WithAlreadyArchivedDept_ShouldThrowException() {
			testDepartment.setIsArchived(false);

			when(departmentRepo.findById(TEST_DEPT_ID)).thenReturn(Optional.of(testDepartment));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> departmentService.restoreArchivedDepartment(TEST_DEPT_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.DEPT_NOT_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_DEPT_ID.toString()));
		}
	}
}
