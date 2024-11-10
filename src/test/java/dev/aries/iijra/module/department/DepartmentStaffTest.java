package dev.aries.iijra.module.department;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentStaffTests {

	@Mock
	private StaffRepository staffRepo;

	@InjectMocks
	private DepartmentStaff departmentStaff;

	private Staff testStaff1;
	private Staff testStaff2;

	@BeforeEach
	void setUp() {
		testStaff1 = TestDataFactory.newStaff();
		testStaff2 = TestDataFactory.newStaff();
	}

	@Test
	@DisplayName("Should return staff list with HOD")
	void returnsStaffListWithHod() {
		Staff testHod = TestDataFactory.newStaff();
		testHod.setHod(true);
		List<Staff> staffList = new ArrayList<>(List.of(testHod, testStaff1, testStaff2));

		when(staffRepo.findAllByDepartmentId(1L)).thenReturn(staffList);

		DepartmentResponse.DepartmentStaff result = departmentStaff.getDepartmentStaff(1L);

		assertEquals(testHod, result.hod());
		assertEquals(2, result.staff().size());
	}

	@Test
	@DisplayName("Should return staff list without HOD")
	void returnsStaffListWithoutHod() {
		List<Staff> staffList = new ArrayList<>(List.of(testStaff1, testStaff2));

		when(staffRepo.findAllByDepartmentId(1L)).thenReturn(staffList);
		DepartmentResponse.DepartmentStaff result = departmentStaff.getDepartmentStaff(1L);

		assertNull(result.hod());
		assertEquals(2, result.staff().size());
	}

	@Test
	@DisplayName("Should return empty staff list")
	void returnsEmptyStaffList() {
		when(staffRepo.findAllByDepartmentId(1L)).thenReturn(Collections.emptyList());

		DepartmentResponse.DepartmentStaff result = departmentStaff.getDepartmentStaff(1L);

		assertNull(result.hod());
		assertEquals(0, result.staff().size());
	}
}
