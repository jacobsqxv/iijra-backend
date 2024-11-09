package dev.aries.iijra.module.staff;

import java.util.Objects;

import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "Staff.withDetails", attributeNodes = {
		@NamedAttributeNode("department"),
		@NamedAttributeNode("user")
})
public class Staff {

	@Id
	@Column(updatable = false, nullable = false, unique = true)
	private String id;

	private String profileImage;

	@Column(nullable = false)
	private String fullName;

	@Column(columnDefinition = "TEXT", length = 500)
	private String bio;

	@OneToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private User user;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@ToString.Exclude
	private Department department;

	@Column(nullable = false)
	private Boolean isHod;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Staff(String profileImage, String fullName, Department department) {
		this.fullName = fullName;
		this.department = department;
		this.profileImage = profileImage;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Staff that = (Staff) o;
		return Objects.equals(getId(), that.getId()) &&
				Objects.equals(getDepartment(), that.getDepartment()) &&
				Objects.equals(getUser(), that.getUser());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getId(), getDepartment(), getUser());
	}
}
