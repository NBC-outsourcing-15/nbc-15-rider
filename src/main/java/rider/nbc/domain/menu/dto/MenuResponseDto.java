package rider.nbc.domain.menu.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.entity.Menu;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponseDto {

    private Long id;
    private String name;
    private String category;
    private Long price;
    private String menuPictureUrl;
    private Long storeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MenuResponseDto fromEntity(Menu menu) {
        return MenuResponseDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .category(menu.getCategory())
                .price(menu.getPrice())
                .menuPictureUrl(menu.getMenuPictureUrl())
                .storeId(menu.getStore().getId())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}