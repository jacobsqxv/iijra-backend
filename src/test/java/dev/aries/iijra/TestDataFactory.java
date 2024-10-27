package dev.aries.iijra;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.department.Department;
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
		return new Staff(
				"ST0001",
				"John Doe",
				newUser(),
				newDepartment());
	}

	public static Department newDepartment() {
		return new Department("Department");
	}
}
