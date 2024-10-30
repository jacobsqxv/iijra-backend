package dev.aries.iijra.module.department;

import dev.aries.iijra.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
	private final DepartmentService service;

	@PostMapping
	@PreAuthorize("hasRole('SYS_ADMIN')")
	public ResponseEntity<Object> addNewDepartment(@Valid @RequestBody DepartmentRequest request) {
		return Response.success(HttpStatus.CREATED, service.addNewDepartment(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SYS_ADMIN', 'HOD')")
	public ResponseEntity<Object> getAllDepartments() {
		return Response.success(HttpStatus.OK, service.getAllDepartments());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getDepartmentById(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, service.getDepartment(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateDepartmentInfo(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
		return Response.success(HttpStatus.OK, service.updateDepartmentInfo(id, request));
	}

	@PutMapping("/{id}/archive")
	@PreAuthorize("hasRole('SYS_ADMIN')")
	public ResponseEntity<Object> archiveDepartment(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, service.archiveDepartment(id));
	}

	@PutMapping("/{id}/restore")
	@PreAuthorize("hasRole('SYS_ADMIN')")
	public ResponseEntity<Object> restoreArchivedDepartment(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, service.restoreArchivedDepartment(id));
	}

}
