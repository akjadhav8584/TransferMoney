package com.dws.challenge.service;

import java.math.BigDecimal;

public interface TransferMoneyService {
	void transferMoney(String accountFromId, String accountToId, BigDecimal amount);
}
