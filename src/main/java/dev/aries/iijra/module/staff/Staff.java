package dev.aries.iijra.module.staff;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.staffprofile.StaffProfile;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "Staff.profile", attributeNodes = @NamedAttributeNode("profile"))
@NamedEntityGraph(name = "Staff.department", attributeNodes = @NamedAttributeNode("department"))
public class Staff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	@ToString.Exclude
	private String password;

	@OneToOne(cascade = CascadeType.ALL)
	@ToString.Exclude
	private StaffProfile profile;

	@ManyToOne
	@ToString.Exclude
	private Department department;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	private Boolean isHod;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Embedded @Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Staff(String email, String password, Department department) {
		this.email = email;
		this.password = password;
		this.department = department;
		this.role = Role.STAFF;
		this.status = Status.ACTIVE;
		this.isHod = false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		Staff staff = (Staff) o;
		return id != null && id.equals(staff.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 31;
	}
}
