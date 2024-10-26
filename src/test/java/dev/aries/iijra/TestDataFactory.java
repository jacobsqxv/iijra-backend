package dev.aries.iijra;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.user.User;

public class TestDataFactory {

	public static User newUser() {
		return User.builder()
				.id(1L)
				.email("test@email.com")
				.password("hashedPassword")
				.role(Role.STAFF)
				.isArchived(false)
				.status(Status.ACTIVE).build();
	}

	public static Staff newStaff() {
		return Staff.builder()
				.id("ST0001")
				.fullName("John Doe").build();
	}
}
