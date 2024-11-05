package dev.aries.iijra.module.admin;

import java.util.Objects;

import dev.aries.iijra.module.user.User;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private User user;

	private String profileImage;

	@Column(nullable = false)
	private String fullName;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Admin(User user, String fullName) {
		this.user = user;
		this.fullName = fullName;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Admin that = (Admin) o;
		return Objects.equals(getId(), that.getId()) &&
				Objects.equals(getFullName(), that.getFullName()) &&
				Objects.equals(getUser(), that.getUser());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getId(), getFullName(), getUser());
	}
}
