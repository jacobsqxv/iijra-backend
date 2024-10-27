package dev.aries.iijra.search;

import java.util.List;

import lombok.Builder;

@Builder
public record GetStaffPage(
		String search,
		List<String> departments,
		String status,
		Boolean isHod
) {
}
