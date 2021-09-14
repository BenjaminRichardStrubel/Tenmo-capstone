package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    AccountDao dao;

    @Autowired
    UserDao userDao;

    @RequestMapping(path = "balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal){
        return dao.getBalance(userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    @RequestMapping(path = "transfers", method = RequestMethod.POST)
    public Transfer transferTo(@RequestBody Transfer transfer){
        return dao.transferTo(transfer);
    }

    @RequestMapping(path = "transfers", method = RequestMethod.GET)
    public List<Transfer> userTransfers(Principal principal){
        return dao.viewTransactions(userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> transferById(@PathVariable int id){
        return dao.viewTransactionById(id);
    }
}
