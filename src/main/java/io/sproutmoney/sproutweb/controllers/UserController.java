package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/10/17


import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsGetRequest;
import com.plaid.client.response.AccountsGetResponse;
import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.PlaidItem;
import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.AccountService;
import io.sproutmoney.sproutweb.services.PlaidItemService;
import io.sproutmoney.sproutweb.services.UserService;
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
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    PlaidItemService plaidItemService;

    private Authentication authentication;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView showUserDashboard(ModelAndView modelAndView) {
        modelAndView.setViewName("dashboard");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current session is user: " + authentication.getName());
        User user = userService.findByEmail(authentication.getName());
        modelAndView.addObject("user", user);

        logger.info("Current user " + user.getEmail() + " has " + user.getAccounts().size() + " accounts.");

        updateAllAccounts("access-development-07809e34-d84f-4d90-a0fe-4cf9972edd4a");
        PlaidItem item = plaidItemService.findByAccessToken("access-development-07809e34-d84f-4d90-a0fe-4cf9972edd4a");
        logger.info("Testing item search, found: " + item);

        return modelAndView;
    }

    public void updateAllAccounts(String plaidAccessToken) {
        PlaidClient plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").developmentBaseUrl().build();
        logger.debug("PlaidClient is " + plaidClient);
        Response<AccountsGetResponse> response = null;
        try {
            response = plaidClient.service().accountsGet(
                    new AccountsGetRequest(plaidAccessToken))
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Account service is " + accountService);

        logger.info("Account: " + accountService.findByPlaidAccountId("E9knBzvQ8PcLYzPrqaekh17EqpZ53rtwYwzBQ"));
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
