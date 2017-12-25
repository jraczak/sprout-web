package io.sproutmoney.sproutweb.controllers;

//  Created by Justin on 12/16/17

import com.google.gson.*;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import io.sproutmoney.sproutweb.data.PlaidItemRepository;
import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.PlaidItem;
import io.sproutmoney.sproutweb.models.User;
import io.sproutmoney.sproutweb.services.AccountService;
import io.sproutmoney.sproutweb.services.PlaidItemService;
import io.sproutmoney.sproutweb.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonSimpleJsonParser;
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
import java.util.Set;

@Controller
public class PlaidController {

    private Logger logger = LoggerFactory.getLogger(PlaidController.class.getSimpleName());

    PlaidClient plaidClient;

    Gson gson;

    @Autowired
    PlaidItemService plaidItemService;
    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/get_plaid_access_token", method = RequestMethod.POST)
    public ResponseEntity<?> getPlaidAccessToken(String publicToken, String institutionName, String institutionId,
                                                 String accounts) {

        // Convert the account metadata to JSON and parse
        //JsonArray jsonArray = new JsonParser().parse(accounts).getAsJsonArray();
        //System.out.println(jsonArray);
        //for (JsonElement object : jsonArray) {
        //    System.out.println(object.getAsJsonObject().get("name"));
        //}

        //JsonArray jsonArray = new JsonArray();
        //jsonArray.add(accounts);
        //System.out.println("jsonArray is " + jsonArray);
        //for (JsonElement object : jsonArray) {
        //    System.out.println(object);
        //}

        logger.debug("Received public token " + publicToken + " from request");
        logger.debug("Attempting to create new item for "+ institutionName);
        System.out.println("Trying to exchange public token " + publicToken + " for access token.");
        System.out.println("Accounts metadata value is " + accounts);

        PlaidItem plaidItem = new PlaidItem();
        User user = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        logger.debug("Found current user " + user.getEmail());
        String accessToken;
        Response<ItemPublicTokenExchangeResponse> response = null;

        // Exchange the public token for the access token
        // TODO: Change this to development environment for now
        logger.debug("Creating Plaid URL and exchanging public token");
        plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
                .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").developmentBaseUrl().build();
        logger.debug("Plaid Client is " + plaidClient);
        System.out.println("Plaid client is " + plaidClient);
        try {
            response = plaidClient.service().itemPublicTokenExchange(
                    new ItemPublicTokenExchangeRequest(publicToken)).execute();
            System.out.println("Inside try block of token exchange.");
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

             // Iterate through the account metadata and create new accounts,
             // save them to user and plaid item
             System.out.println("Attempting to create accounts from metadata");
             JsonArray jsonArray = new JsonParser().parse(accounts).getAsJsonArray();
             System.out.println("Converted metadata string to jsonArray: " + jsonArray);
             for (JsonElement object : jsonArray) {
                 JsonObject a = object.getAsJsonObject();
                 System.out.println("Attempting to create account for " + a.get("name"));
                 System.out.println("Testing querying type: "+ a.get("type").toString());

                 // Use getAsString not getString to prevent wrapping in quotes when saving
                 Account account = new Account(user, a.get("id").getAsString(),
                                                a.get("type").getAsString(),
                                                institutionId, a.get("name").getAsString(),
                                                plaidItem);
                 System.out.println("Saving new account for " + a.get("name"));
                 accountService.saveAccount(account);
             }
         }
         return ResponseEntity.ok(plaidItem.getInsitutionName());
    }
}
