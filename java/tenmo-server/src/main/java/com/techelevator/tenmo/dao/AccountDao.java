package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    BigDecimal getBalance(int id);

    Transfer transferTo(Transfer transfer);

    List<Transfer> viewTransactionById(int id);

    List<Transfer> viewTransactions(int id);



}