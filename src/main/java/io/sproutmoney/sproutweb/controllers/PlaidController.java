package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/16/17

import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import io.sproutmoney.sproutweb.data.PlaidItemRepository;
import io.sproutmoney.sproutweb.models.PlaidItem;
import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.PlaidItemService;
import io.sproutmoney.sproutweb.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit2.Response;

import java.awt.*;
import java.io.IOException;

import static com.sun.tools.doclint.Entity.prod;

@Controller
public class PlaidController {

    private Logger logger = LoggerFactory.getLogger(PlaidController.class.getSimpleName());

    PlaidClient plaidClient;

    @Autowired
    PlaidItemService plaidItemService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/get_plaid_access_token", method = RequestMethod.POST)
    public ResponseEntity<?> getPlaidAccessToken(String publicToken, String institutionName, String institutionId) {

        logger.debug("Received public token " + publicToken + " from request");
        logger.debug("Attempting to create new item for "+ institutionName);
        System.out.println("Trying to exchange token for public token " + publicToken);

        PlaidItem plaidItem = new PlaidItem();
        User user = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        logger.debug("Found current user " + user.getEmail());
        String accessToken;
        Response<ItemPublicTokenExchangeResponse> response = null;

        // Exchange the public token for the access token
        logger.debug("Creating Plaid URL and exchanging public token");
        plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").developmentBaseUrl().build();
        logger.debug("Plaid Client is " + plaidClient);
        System.out.println("Plaid client is " + plaidClient);
        try {
            response = plaidClient.service().itemPublicTokenExchange(
                    new ItemPublicTokenExchangeRequest(publicToken)).execute();
            System.out.println("Inside try block.");
            System.out.println("Response is " + response.body());
            logger.debug(response.message());
        } catch (IOException e) {
            e.printStackTrace();
        }

         if (response.isSuccessful()) {
             accessToken = response.body().getAccessToken();

             logger.debug("Creating new plaid item for access token " + accessToken);
             // Create and save the new plaid item using the access token
             plaidItem.setAccessToken(accessToken);
             plaidItem.setExternalPlaidItemId(response.body().getItemId());
             plaidItem.setUser(user);
             plaidItem.setInsitutionName(institutionName);
             plaidItem.setInstitutionId(institutionId);
             logger.debug("Saving new plaid item to database");
             plaidItemService.savePlaidItem(plaidItem);
             logger.debug("Plaid Item saved to the database as " + plaidItem.getId());
         }
         return ResponseEntity.ok(plaidItem.getInsitutionName());
    }
}