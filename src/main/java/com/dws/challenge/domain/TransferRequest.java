package com.dws.challenge.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferRequest {
	@NotBlank
	private String accountFromId;

	@NotBlank
	private String accountToId;

	@Min(value = 1, message = "Transfer amount must be greater than 0")
	private BigDecimal amount;
}
