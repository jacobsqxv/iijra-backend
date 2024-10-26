package dev.aries.iijra.module.staff;

import java.util.ArrayList;
import java.util.List;

import dev.aries.iijra.enums.Status;
import dev.aries.iijra.search.GetStaffPage;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

public final class StaffSpecification {

	private StaffSpecification() {
	}

	public static Specification<Staff> buildSpecification(GetStaffPage request, boolean isArchived) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Always filter out archived staff
			predicates.add(cb.equal(root.get("user").get("isArchived"), isArchived));

			// Handle search term (name OR email)
			if (request.search() != null && !request.search().trim().isEmpty()) {
				String searchTerm = "%" + request.search().toLowerCase() + "%";
				Predicate namePredicate = cb.like(
						cb.lower(root.get("fullName")),
						searchTerm
				);
				Predicate emailPredicate = cb.like(
						cb.lower(root.get("user").get("email")),
						searchTerm
				);
				predicates.add(cb.or(namePredicate, emailPredicate));
			}

			// Handle departments
			if (request.departments() != null && !request.departments().isEmpty()) {
				CriteriaBuilder.In<String> inClause = cb.in(root.get("department").get("name"));
				request.departments().forEach(inClause::value);
				predicates.add(inClause);
			}

			// Handle status
			if (request.status() != null && !request.status().trim().isEmpty()) {
				predicates.add(cb.equal(
						root.get("status"),
						Status.valueOf(request.status().toUpperCase())
				));
			}

			// Handle isHod (assuming it's not nullable in the request)
			if (request.isHod() != null) {
				predicates.add(cb.equal(root.get("isHod"), request.isHod()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
