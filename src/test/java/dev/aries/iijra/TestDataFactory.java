package dev.aries.iijra;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staffprofile.StaffProfile;

public class TestDataFactory {

	public static Staff newStaff() {
		return Staff.builder()
				.id(1L)
				.email("test@email.com")
				.password("hashedPassword")
				.role(Role.STAFF)
				.profile(newStaffProfile())
				.isActive(true)
				.status(Status.ACTIVE).build();
	}

	public static StaffProfile newStaffProfile() {
		return StaffProfile.builder()
				.id("ST0001")
				.fullName("John Doe").build();
	}
}
