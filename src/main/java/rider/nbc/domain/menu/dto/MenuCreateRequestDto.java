package rider.nbc.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.store.entity.Store;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCreateRequestDto {

	@NotBlank(message = "메뉴 이름은 필수입니다.")
	private String name;

	@NotBlank(message = "메뉴 카테고리는 필수입니다.")
	private String category;

	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private Long price;

	private String menuPictureUrl;

	public Menu toEntity(Store store) {
		return Menu.builder()
			.name(name)
			.category(category)
			.price(price)
			.menuPictureUrl(menuPictureUrl)
			.store(store)
			.build();
	}
}