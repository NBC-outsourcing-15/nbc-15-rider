package rider.nbc.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.entity.Menu;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuUpdateRequestDto {
    
    @NotBlank(message = "메뉴 이름은 필수입니다.")
    private String name;
    
    @NotBlank(message = "메뉴 카테고리는 필수입니다.")
    private String category;
    
    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    private Long price;
    
    private String menuPictureUrl;
}