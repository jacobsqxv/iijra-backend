package dev.aries.iijra.module.department;

import java.util.List;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentStaff {
	private final StaffRepository staffRepo;

	public DepartmentResponse.DepartmentStaff getDepartmentStaff(Long departmentId) {
		List<Staff> staffList = getStaff(departmentId);
		Staff hod = null;
		for (Staff s : staffList) {
			if (Boolean.TRUE.equals(s.getIsHod())) hod = s;
		}

		if (hod != null) staffList.remove(hod);

		return new DepartmentResponse.DepartmentStaff(hod, staffList);
	}

	private List<Staff> getStaff(Long departmentId) {
		return staffRepo.findAllByDepartmentId(departmentId);
	}

}
