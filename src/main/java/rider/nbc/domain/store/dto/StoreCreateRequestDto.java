package rider.nbc.domain.store.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.store.entity.OperatingHours;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreAddress;
import rider.nbc.domain.store.entity.StoreStatus;
import rider.nbc.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCreateRequestDto {
    
    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;
    
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
    
    @NotBlank(message = "시/도는 필수입니다.")
    private String city;
    
    @NotBlank(message = "구/군은 필수입니다.")
    private String district;
    
    @NotBlank(message = "상세주소는 필수입니다.")
    private String detailedAddress;
    
    private String storePictureUrl;
    
    private String introduce;
    
    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;
    
    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closeTime;
    
    public Store toEntity(User owner) {
        return Store.builder()
                .name(name)
                .category(category)
                .address(new StoreAddress(city, district, detailedAddress))
                .storePictureUrl(storePictureUrl)
                .introduce(introduce)
                .operatingHours(new OperatingHours(openTime, closeTime))
                .storeStatus(StoreStatus.CLOSED) // 초기 상태는 CLOSED로 설정
                .owner(owner)
                .build();
    }
}