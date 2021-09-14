package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountJdbcDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public AccountJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(int id) {
        Balance balance = new Balance();

        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        while (result.next()) {
            balance.setBalance(result.getBigDecimal("balance"));
        }
        return balance.getBalance();
    }

    @Override
    public Transfer transferTo(Transfer transfer) {
        double balance = 0;
        //Get balance to see if sender has enough funds
        String sqlBalance = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet balanceResult = jdbcTemplate.queryForRowSet(sqlBalance, transfer.getAccountFrom());
        while (balanceResult.next()) {
            balance = balanceResult.getDouble("balance");
        }
        if (BigDecimal.valueOf(balance).compareTo(transfer.getAmount()) >= 0) {
            //Approved status if there are enough funds
            String sqlTransferStatus = "SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = ?;";
            transfer.setTransferStatus(jdbcTemplate.queryForObject(sqlTransferStatus, Integer.class, "Approved"));

            String sqlTransferType = "SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc = ?;";
            transfer.setTransferType(jdbcTemplate.queryForObject(sqlTransferType, Integer.class, "Send"));
        } else {
            //Rejected status if not enough funds
            String sqlTransferStatus = "SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = ?;";
            transfer.setTransferStatus(jdbcTemplate.queryForObject(sqlTransferStatus, Integer.class, "Rejected"));
            System.out.println("Your transfer was unable to be completed.");
            return null;
        }
        //Get account numbers for sender and recipient from UserId
        int fromUserId = transfer.getAccountFrom();
        String fromAccountSql = "SELECT account_id FROM accounts WHERE user_id = ?";
        transfer.setAccountFrom(jdbcTemplate.queryForObject(fromAccountSql, Integer.class, fromUserId));
        int toUserId = transfer.getAccountTo();
        String toAccountSql = "SELECT account_id FROM accounts WHERE user_id = ?";
        transfer.setAccountTo(jdbcTemplate.queryForObject(toAccountSql, Integer.class, toUserId));

        //update the accounts
        String sqlFrom = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?;";
        jdbcTemplate.update(sqlFrom, transfer.getAmount(), transfer.getAccountFrom());
        String sqlTo = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?;";
        jdbcTemplate.update(sqlTo, transfer.getAmount(), transfer.getAccountTo());

        //update the transfers table
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                " VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql, transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAccountFrom(),
                transfer.getAccountTo(), transfer.getAmount());
        return transfer;
    }

    @Override
    public List<Transfer> viewTransactions(int id) {
        List<Transfer> transactions = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers a" +
                " JOIN accounts b ON a.account_from  = b.account_id OR a.account_to  = b.account_id" +
                " WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            transactions.add(mapRowToTransfer(results));
        }
        return transactions;
    }

    @Override
    public List<Transfer> viewTransactionById(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferType(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatus(rowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }

}

