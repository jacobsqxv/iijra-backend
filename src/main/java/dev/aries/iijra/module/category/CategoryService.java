package dev.aries.iijra.module.category;

import java.util.List;

import dev.aries.iijra.constant.ExceptionConstant;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepo;

	@Transactional
	public CategoryResponse addNewCategory(CategoryRequest request) {
		validateCategoryName(request.name());
		Category newCategory = new Category(request.name());
		categoryRepo.save(newCategory);
		log.info("New category added: {}", newCategory.getId());
		return CategoryResponse.toResponse(newCategory);
	}

	public Category getCategory(Long id){
		return categoryRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.CATEGORY_ID_DOESNT_EXIST + id));
	}

	public List<CategoryResponse> getAllCategories() {
		return categoryRepo.findAll().stream()
				.map(CategoryResponse::toResponse)
				.toList();
	}

	public CategoryResponse getCategoryById(Long id) {
		Category category = getCategory(id);
		return CategoryResponse.toResponse(category);
	}

	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = getCategory(id);
		validateCategoryName(request.name());
		category.setName(request.name());
		categoryRepo.save(category);
		return CategoryResponse.toResponse(category);
	}

	public String archiveCategory(Long id) {
		Category category = getCategory(id);
		if (Boolean.TRUE.equals(category.getArchived())) {
			throw new IllegalStateException(ExceptionConstant.CATEGORY_ALREADY_ARCHIVED + id);
		}
		category.archive();
		categoryRepo.save(category);
		return String.format("Category %s has been archived", category.getName());
	}

	public String restoreArchivedCategory(Long id) {
		Category category = getCategory(id);
		if (Boolean.FALSE.equals(category.getArchived())) {
			throw new IllegalStateException(ExceptionConstant.CATEGORY_NOT_ARCHIVED + id);
		}
		category.restore();
		categoryRepo.save(category);
		return String.format("Category %s has been restored", category.getName());
	}

	private void validateCategoryName(String name) {
		if (categoryRepo.existsByName(name)){
			throw new EntityExistsException(ExceptionConstant.CATEGORY_NAME_ALREADY_EXISTS + name);
		}
	}
}
