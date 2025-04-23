package rider.nbc.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class StoreAddress {

	@Column(name = "city", nullable = false)
	private String city; // 시/도

	@Column(name = "district", nullable = false)
	private String district; // 구/군

	@Column(name = "detailed_address", nullable = false)
	private String detailedAddress; // 상세주소
}
