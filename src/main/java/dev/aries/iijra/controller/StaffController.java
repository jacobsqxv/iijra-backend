package dev.aries.iijra.controller;

import dev.aries.iijra.global.Response;
import dev.aries.iijra.search.GetStaffPage;
import dev.aries.iijra.module.staff.StaffRequest;
import dev.aries.iijra.module.staff.StaffResponse;
import dev.aries.iijra.module.staff.StaffService;
import dev.aries.iijra.global.PageResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {
	private final StaffService service;

	@PostMapping
	@PreAuthorize("hasAnyRole('SYS_ADMIN', 'HOD')")
	public ResponseEntity<Object> addNewStaff(@RequestBody StaffRequest request) {
		return Response.success(HttpStatus.CREATED,service.addNewStaff(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SYS_ADMIN', 'HOD')")
	public ResponseEntity<PageResponse<StaffResponse>> getAllStaff(GetStaffPage request, @PageableDefault Pageable pageable) {
		return PageResponse.of(service.getAllStaff(request, pageable), HttpStatus.OK);
	}

}
