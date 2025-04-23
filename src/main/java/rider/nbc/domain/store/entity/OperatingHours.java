package rider.nbc.domain.store.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OperatingHours {

	@Column(name = "open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "close_time", nullable = false)
	private LocalTime closeTime;

	protected OperatingHours() {
	}

	public OperatingHours(LocalTime openTime, LocalTime closeTime) {
		this.openTime = openTime;
		this.closeTime = closeTime;
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public LocalTime getCloseTime() {
		return closeTime;
	}
}