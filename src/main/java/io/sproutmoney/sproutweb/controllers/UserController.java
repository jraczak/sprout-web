package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/10/17


import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsBalanceGetRequest;
import com.plaid.client.request.AccountsGetRequest;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.AccountsBalanceGetResponse;
import com.plaid.client.response.AccountsGetResponse;
import com.plaid.client.response.TransactionsGetResponse;
import io.sproutmoney.sproutweb.models.*;
import io.sproutmoney.sproutweb.services.*;
import org.hibernate.type.CharacterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import retrofit2.Response;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    PlaidItemService plaidItemService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionCategoryService transactionCategoryService;

    private Authentication authentication;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView showUserDashboard(ModelAndView modelAndView) {
        modelAndView.setViewName("dashboard");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current session is user: " + authentication.getName());
        User user = userService.findByEmail(authentication.getName());
        modelAndView.addObject("user", user);
        modelAndView.addObject("accounts", accountService.findAllByUserOrderByInstitutionNameAsc(user));

        logger.info("Current user " + user.getEmail() + " has " + user.getAccounts().size() + " accounts.");

        updateAllAccounts(user);

        updateAccountBalances(user);

        syncAccountTransactions(user);

        return modelAndView;
    }

    public void updateAllAccounts(User user) {
        //TODO: Abstract plaid client into utility somewhere so we don't have to initialize all the time
        //TODO: Change this back to development environment
        PlaidClient plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").sandboxBaseUrl().build();
        logger.debug("PlaidClient is " + plaidClient);
        Response<AccountsGetResponse> response = null;

        Set<PlaidItem> userItems = user.getPlaidItems();
        for (PlaidItem item : userItems) {

            try {
                logger.info("Attempting to fetch accounts for item with access token " + item.getAccessToken());
                response = plaidClient.service().accountsGet(
                        new AccountsGetRequest(item.getAccessToken()))
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Account service is " + accountService);

            if (response != null) {
                List<com.plaid.client.response.Account> accounts = response.body().getAccounts();
                for (com.plaid.client.response.Account account : accounts) {
                    Account sproutAccount = accountService.findByPlaidAccountId(account.getAccountId());
                    if (sproutAccount != null) {
                        logger.info(account.getSubtype());
                        logger.info(account.getOfficialName());
                        sproutAccount.setAccountSubtype(account.getSubtype());
                        sproutAccount.setMask(account.getMask());
                        sproutAccount.setOfficialName(account.getOfficialName());
                        accountService.saveAccount(sproutAccount);
                    } else logger.error("No account found with plaid id " + account.getAccountId());
                }
            } else logger.error("There was no client response");
        }
    }

    public void updateAccountBalances(User user) {
        Set<PlaidItem> items = user.getPlaidItems();
        if (!items.isEmpty()) {
            PlaidClient plaidClient = PlaidClient.newBuilder()
                    .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                    .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").sandboxBaseUrl().build();
            logger.debug("PlaidClient is " + plaidClient);
            for (PlaidItem item : items) {
                Response<AccountsBalanceGetResponse> response = null;
                try {
                    response = plaidClient.service()
                            .accountsBalanceGet(new AccountsBalanceGetRequest(item.getAccessToken()))
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    logger.info(response.toString());
                    List<com.plaid.client.response.Account> accounts = response.body().getAccounts();
                    for (com.plaid.client.response.Account account : accounts) {
                        Account sproutAccount = accountService.findByPlaidAccountId(account.getAccountId());
                        if (sproutAccount != null) {
                            logger.info("Updating sprout account " + sproutAccount.getId());
                            if ( sproutAccount.getAccountType().equals("depository")) {
                                sproutAccount.setAvailableBalance(account.getBalances().getAvailable());
                            }
                            sproutAccount.setCurrentBalance(account.getBalances().getCurrent());
                            if (sproutAccount.getAccountSubtype().equals("credit card")) {
                                sproutAccount.setAccountLimit(account.getBalances().getLimit());
                            }
                            accountService.saveAccount(sproutAccount);
                        } else logger.error("No account found with plaid id " + account.getAccountId());
                    }
                } else logger.error("There was no client response to parse");
            }
        }
    }

    // Method to sync transactions from Plaid
    // TODO: Figure out how to do heavy lifting of requesting all transactions from the past year
    // (Plaid limits response to 500 transactions per request)
    public void syncAccountTransactions(User user) {

        // Make sure the user has items/accounts before processing
        Set<PlaidItem> userItems = user.getPlaidItems();
        if (!userItems.isEmpty()) {

            PlaidClient plaidClient = PlaidClient.newBuilder()
                    .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                    .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").sandboxBaseUrl().build();
            Response<TransactionsGetResponse> response = null;

            //TODO: Add all these calendar bits to Snippets
            // Set up the date formats to send to the client
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            Date startDate = new Date(), endDate = new Date();
            Date now = new Date();
            Date thirtyDaysAgo;
            calendar.setTime(now);
            calendar.add(Calendar.DATE, -30);
            thirtyDaysAgo = calendar.getTime();
            LocalDate localDateEnd = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDateStart = thirtyDaysAgo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            //TODO: Update these dates to be based on dates of last transaction(s) synced
            try {
                endDate = dateFormat.parse(localDateEnd.getYear() + "-" +
                        localDateEnd.getMonthValue() + "-" + localDateEnd.getDayOfMonth());
                startDate = dateFormat.parse(localDateStart.getYear() + "-" +
                        localDateStart.getMonthValue() + "-" + localDateStart.getDayOfMonth());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            logger.info("Initialized start date: " + startDate + " and end date " + endDate);

            ArrayList<String> accountIdList = new ArrayList<>();
            // Iterate through the items
            //TODO: Handle no plaid items and no accounts in a plaid item cases
            for (PlaidItem item : userItems) {
                for (Account account : item.getAccounts()) {
                    accountIdList.clear(); // Remove the previous account ID value
                    accountIdList.add(account.getPlaidAccountId());
                    try {
                        response = plaidClient.service().transactionsGet(
                                new TransactionsGetRequest(item.getAccessToken(),
                                        startDate,
                                        endDate)
                                .withAccountIds(accountIdList)
                        ).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null && response.isSuccessful()) {
                        for (com.plaid.client.response.Account a : response.body().getAccounts()) {
                            logger.info("Syncing transactions for account: " + a.getName());
                            //TODO: Update this to include functionality to update if existing transaction is found
                            for (TransactionsGetResponse.Transaction transaction : response.body().getTransactions()) {
                                Transaction existingTransaction = transactionService.findByPlaidTransactionId(transaction.getTransactionId());
                                if (existingTransaction == null) {
                                    logger.info("Attempting to create transaction for " + transaction.getName() +
                                            " for " + transaction.getAmount() +
                                            " on " + transaction.getDate());
                                    Transaction newTransaction = new Transaction(user, account, transaction.getName(),
                                            transaction.getCategory(),
                                            transaction.getCategoryId(),
                                            transaction.getAccountId(),
                                            transaction.getTransactionId(),
                                            transaction.getTransactionType(),
                                            transaction.getAmount(),
                                            transaction.getDate(),
                                            transaction.getLocation().getAddress(),
                                            transaction.getLocation().getCity(),
                                            transaction.getLocation().getState(),
                                            transaction.getLocation().getZip(),
                                            transaction.getPending());
                                    transactionService.saveTransaction(newTransaction);
                                    logger.info("Saved new transaction with ID " + newTransaction.getId());
                                } else logger.info("Existing transaction found for " + transaction.getTransactionId() + "; skipping");
                            }
                        }
                    }
                }
            }

        } else logger.info("No plaid items found for user " + user.getEmail());
    }

}
