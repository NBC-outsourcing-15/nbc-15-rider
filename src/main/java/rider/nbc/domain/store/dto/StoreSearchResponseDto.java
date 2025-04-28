package rider.nbc.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.store.entity.Store;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSearchResponseDto {
    private Long id;
    private String name;
    private String storePicture;

    public static StoreSearchResponseDto fromEntity(Store store) {
        return StoreSearchResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .storePicture(store.getStorePictureUrl())
                .build();
    }
}