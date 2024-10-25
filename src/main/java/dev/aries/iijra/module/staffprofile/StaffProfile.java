package dev.aries.iijra.module.staffprofile;

import java.util.Objects;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "StaffProfile.staff", attributeNodes = @NamedAttributeNode("staff"))
public class StaffProfile {
	@Id
	@Column(updatable = false, nullable = false)
	private String id;

	private String profileImage;

	@Column(nullable = false)
	private String fullName;

	@Column(columnDefinition = "TEXT", length = 500)
	private String bio;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "profile")
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Staff staff;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public StaffProfile(String id, String fullName, Staff staff) {
		this.id = id;
		this.fullName = fullName;
		this.staff = staff;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (!thisEffectiveClass.equals(oEffectiveClass)) {
			return false;
		}
		if (!(o instanceof StaffProfile that)) {
			return false;
		}
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
