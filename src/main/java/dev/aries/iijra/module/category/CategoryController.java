package dev.aries.iijra.module.category;

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
@RequestMapping("/api/v1/sops/categories")
@PreAuthorize("hasRole('SYS_ADMIN')")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<Object> addNewCategory(@RequestBody @Valid CategoryRequest request) {
		return Response.success(HttpStatus.CREATED, categoryService.addNewCategory(request));
	}

	@GetMapping
	public ResponseEntity<Object> getAllCategories() {
		return Response.success(HttpStatus.OK, categoryService.getAllCategories());
	}

	@GetMapping("{id}")
	public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, categoryService.getCategoryById(id));
	}

	@PutMapping("{id}")
	public ResponseEntity<Object> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
		return Response.success(HttpStatus.OK, categoryService.updateCategory(id, request));
	}

	@PutMapping("{id}/archive")
	public ResponseEntity<Object> archiveCategory(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, categoryService.archiveCategory(id));
	}

	@PutMapping("{id}/restore")
	public ResponseEntity<Object> restoreArchivedCategory(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, categoryService.restoreArchivedCategory(id));
	}

}
