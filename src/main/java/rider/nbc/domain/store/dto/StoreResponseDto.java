package rider.nbc.domain.store.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponseDto {

	private Long id;
	private String name;
	private String category;
	private String city;
	private String district;
	private String detailedAddress;
	private String storePictureUrl;
	private String introduce;
	private LocalTime openTime;
	private LocalTime closeTime;
	private StoreStatus storeStatus;
	private Long ownerId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static StoreResponseDto fromEntity(Store store) {
		return StoreResponseDto.builder()
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
			.build();
	}
}