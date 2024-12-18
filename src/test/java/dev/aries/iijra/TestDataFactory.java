package dev.aries.iijra;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.category.Category;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.utility.Auditing;

public class TestDataFactory {

	public static User newUser() {
		return User.builder()
				.id(1L)
				.email("test@email.com")
				.password("hashedPassword")
				.role(Role.STAFF)
				.archived(false)
				.status(Status.ACTIVE).build();
	}

	public static Staff newStaff() {
		return Staff.builder()
				.id("ST0001")
				.fullName("John Doe")
				.user(newUser())
				.department(newDepartment())
				.auditing(new Auditing())
				.hod(false)
				.build();
	}

	public static Department newDepartment() {
		return new Department("Department");
	}

	public static Category newCategory() {
		return new Category("Category");
	}
}
