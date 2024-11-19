package com.dws.challenge.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferMoneyService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class TransferMoneycontroller {

	@Autowired
	TransferMoneyService transferMoneyService;
	@Autowired(required = false)
	NotificationService notificationService;
	@Autowired
	AccountsService accountsService;

	@PostMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferMoney(@RequestBody @Valid TransferRequest transferRequest) {
		log.info("Transferring money from account {} to account {}", transferRequest.getAccountFromId(),
				transferRequest.getAccountToId());

		try {
			// Perform the money transfer
			transferMoneyService.transferMoney(transferRequest.getAccountFromId(), transferRequest.getAccountToId(),
					transferRequest.getAmount());

			// Fetch account details for notifications
			Account accountFrom = accountsService.getAccount(transferRequest.getAccountFromId());
			Account accountTo = accountsService.getAccount(transferRequest.getAccountToId());

			// Notify both account holders
			notificationService.notifyAboutTransfer(accountFrom, String.format("Transferred %.2f to account %s",
					transferRequest.getAmount(), transferRequest.getAccountToId()));
			notificationService.notifyAboutTransfer(accountTo, String.format("Received %.2f from account %s",
					transferRequest.getAmount(), transferRequest.getAccountFromId()));

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			log.error("Error during transfer: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
