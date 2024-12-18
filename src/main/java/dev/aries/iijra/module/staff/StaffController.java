package dev.aries.iijra.module.staff;

import dev.aries.iijra.global.PageResponse;
import dev.aries.iijra.global.Response;
import dev.aries.iijra.search.GetStaffPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/staff")
@RequiredArgsConstructor
public class StaffController {
	private final StaffService service;

	@PostMapping
	@PreAuthorize("hasAnyRole('SYS_ADMIN', 'HOD')")
	public ResponseEntity<Object> addNewStaff(@Valid @RequestBody StaffRequest request) {
		return Response.success(HttpStatus.CREATED, service.addNewStaff(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SYS_ADMIN', 'HOD')")
	public ResponseEntity<PageResponse<StaffResponse>> getAllStaff(GetStaffPage request, @PageableDefault Pageable pageable) {
		return PageResponse.of(service.getAllStaff(request, pageable), HttpStatus.OK);
	}

	@GetMapping("{id}")
	public ResponseEntity<Object> getStaffById(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, service.getStaffById(id));
	}

	@PutMapping("{id}")
	public ResponseEntity<Object> updateStaffInfo(@PathVariable Long id, @ModelAttribute StaffUpdateRequest request) {
		return Response.success(HttpStatus.OK, service.updateStaffInfo(id, request));
	}
}
