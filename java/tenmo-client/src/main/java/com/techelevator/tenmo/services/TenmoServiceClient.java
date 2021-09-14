package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.view.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class TenmoServiceClient {

    private AuthenticatedUser currentUser;

    private final String base_url;
    private final RestTemplate restTemplate = new RestTemplate();

    public TenmoServiceClient(String base_url, AuthenticatedUser user) {
        this.base_url = base_url;
        this.currentUser = user;
    }

    public BigDecimal viewCurrentBalanceRequest(String token) {
        return restTemplate.exchange(base_url + "balance", HttpMethod.GET, makeEntity(token), BigDecimal.class).getBody();
    }

    public Transfer sendBucks(String token, ConsoleService console) {
        User[] users = restTemplate.exchange(base_url + "users", HttpMethod.GET, makeEntity(token), User[].class).getBody();
        if (users == null) {
            return null;
        }
        for (User user : users) {
            System.out.println(user.getId() + " " + user.getUsername());
        }
        Integer recipient;
        BigDecimal amount;
        Transfer transfer = new Transfer();
        recipient = console.getUserInputInteger("Enter the ID of the user you are sending to (0 to cancel): ");
        transfer.setAccountTo(recipient);
        amount = BigDecimal.valueOf(console.getUserInputInteger("Enter amount: "));

        transfer.setAmount(amount);
        int userId = currentUser.getUser().getId();
        transfer.setAccountFrom(userId);
        Transfer finalTransfer = restTemplate.exchange(base_url + "transfers", HttpMethod.POST, makeEntityTransfer(token, transfer), Transfer.class, transfer).getBody();
        if (finalTransfer != null) {
            System.out.println("Your transfer was completed successfully!");
        } else {
            System.out.println("Your transfer was unable to be completed.");
        }
        return finalTransfer;
    }

    public Transfer[] viewTransactions(String token) {
        Transfer[] transfers = restTemplate.exchange(base_url + "transfers", HttpMethod.GET, makeEntity(token), Transfer[].class).getBody();
        System.out.println("----------------------------");
        System.out.println("Transfers");
        System.out.println("ID" + "\t\t" + "From/To" + "\t\t" + "Amount");
        System.out.println("----------------------------");
        for (Transfer transfer : transfers) {
            System.out.println(transfer.getTransferId() + "\t From: " + transfer.getAccountFrom() + "\t\t" + "$ " + transfer.getAmount());
            System.out.println("\t\t To: " + transfer.getAccountTo());
        }
        return restTemplate.exchange(base_url + "transfers", HttpMethod.GET, makeEntity(token), Transfer[].class).getBody();
    }

    public Transfer[] viewTransactionById(String token, int id) {
        Transfer[] transfers = restTemplate.exchange(base_url + "transfers/" + id, HttpMethod.GET, makeEntity(token), Transfer[].class).getBody();
        System.out.println("----------------------------");
        System.out.println("Transfer Details");
        System.out.println("----------------------------");
        for (Transfer transfer : transfers) {
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + transfer.getAccountFrom());
            System.out.println("To: " + transfer.getAccountTo());
            System.out.println("Type: " + transfer.getTransferType());
            System.out.println("Status: " + transfer.getTransferStatus());
            System.out.println("Amount: $" + transfer.getAmount());
        }
        return restTemplate.exchange(base_url + "transfers/" + id, HttpMethod.GET, makeEntity(token), Transfer[].class).getBody();
    }

    private HttpEntity<Transfer> makeEntity(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(httpHeaders);
    }

    private HttpEntity<Transfer> makeEntityTransfer(String token, Transfer transfer) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, httpHeaders);
    }
}
