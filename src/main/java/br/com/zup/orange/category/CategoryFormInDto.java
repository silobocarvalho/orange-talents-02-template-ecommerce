package br.com.zup.orange.category;

import javax.validation.constraints.NotBlank;

import br.com.zup.orange.user.User;
import br.com.zup.orange.validator.UniqueValue;

public class CategoryFormInDto {

	@NotBlank
	@UniqueValue(domainClass = Category.class, fieldName = "name")
	private String name;

	Category category;
	
	public CategoryFormInDto(@NotBlank String name, Category category) {
		this.name = name;
		this.category = category;
	}



	public Category toModel() {
		return new Category(this.name, this.category);
	}



	public String getName() {
		return this.name;
	}

}
