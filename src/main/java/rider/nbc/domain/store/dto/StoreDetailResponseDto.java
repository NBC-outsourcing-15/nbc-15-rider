package rider.nbc.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.dto.MenuResponseDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDetailResponseDto {

    private Long id;
    private String name;
    private String category;
    private String city;
    private String district;
    private String detailedAddress;
    private String storePictureUrl;
    private String introduce;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime closeTime;
    private StoreStatus storeStatus;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MenuResponseDto> menus;

    public static StoreDetailResponseDto fromEntity(Store store) {
        return StoreDetailResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .city(store.getAddress().getCity())
                .district(store.getAddress().getDistrict())
                .detailedAddress(store.getAddress().getDetailedAddress())
                .storePictureUrl(store.getStorePictureUrl())
                .introduce(store.getIntroduce())
                .openTime(store.getOperatingHours().getOpenTime())
                .closeTime(store.getOperatingHours().getCloseTime())
                .storeStatus(store.getStoreStatus())
                .ownerId(store.getOwner().getId())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .menus(store.getMenus().stream()
                        .map(MenuResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}