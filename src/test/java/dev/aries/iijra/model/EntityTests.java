package dev.aries.iijra.model;

import dev.aries.iijra.module.admin.Admin;
import dev.aries.iijra.module.category.Category;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.sop.SOP;
import dev.aries.iijra.module.sop.sopstaff.SOPStaff;
import dev.aries.iijra.module.sop.sopversion.SOPVersion;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.token.Token;
import dev.aries.iijra.module.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityTests {

	@Test
	@DisplayName("Admin equals and hashCode contract")
	void adminEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(Admin.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "fullName", "user")
				.verify();
	}

	@Test
	@DisplayName("Category equals and hashCode contract")
	void categoryEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(Category.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "name")
				.verify();
	}

	@Test
	@DisplayName("Department equals and hashCode contract")
	void departmentEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(Department.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "name")
				.verify();
	}

	@Test
	@DisplayName("Staff equals and hashCode contract")
	void staffEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(Staff.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "department", "user")
				.verify();
	}

	@Test
	@DisplayName("Token equals and hashCode contract")
	void tokenEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(Token.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "value", "user")
				.verify();
	}

	@Test
	@DisplayName("User equals and hashCode contract")
	void userEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(User.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "email")
				.verify();
	}

	@Test
	@DisplayName("SOP equals and hashCode contract")
	void sopEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(SOP.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "title")
				.verify();
	}

	@Test
	@DisplayName("SOPVersion equals and hashCode contract")
	void sopVersionEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(SOPVersion.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "sop")
				.verify();
	}

	@Test
	@DisplayName("SOPStaff equals and hashCode contract")
	void sopStaffEqualsAndHashCodeContract() {
		EqualsVerifier
				.simple()
				.forClass(SOPStaff.class)
				.withIgnoredAnnotations(Entity.class, Id.class)
				.withOnlyTheseFields("id", "sop")
				.verify();
	}
}
