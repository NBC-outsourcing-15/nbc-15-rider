package rider.nbc.domain.store.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.store.entity.StoreStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreUpdateRequestDto {

	@NotNull(message = "가게 이름은 필수입니다.")
	private String name;

	@NotNull(message = "카테고리는 필수입니다.")
	private String category;

	@NotNull(message = "시/도는 필수입니다.")
	private String city;

	@NotNull(message = "구/군은 필수입니다.")
	private String district;

	@NotNull(message = "상세주소는 필수입니다.")
	private String detailedAddress;

	private String storePictureUrl;

	private String introduce;

	@NotNull(message = "오픈 시간은 필수입니다.")
	private LocalTime openTime;

	@NotNull(message = "마감 시간은 필수입니다.")
	private LocalTime closeTime;

	@NotNull(message = "최소 배달 가격은 필수입니다.")
	private Long minDeliveryPrice;

	@NotNull(message = "상태 정보는 필수입니다.")
	private StoreStatus storeStatus;
}