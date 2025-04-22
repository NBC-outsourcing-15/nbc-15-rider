package rider.nbc.domain.review.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MenuReview {
	private Long MenuId;
	private String content;
}
