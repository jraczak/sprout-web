package io.sproutmoney.sproutweb.util;

//  Created by Justin on 1/13/18

import com.plaid.client.PlaidClient;
import com.plaid.client.request.CategoriesGetRequest;
import com.plaid.client.response.CategoriesGetResponse;
import io.sproutmoney.sproutweb.data.TransactionCategoryRepository;
import io.sproutmoney.sproutweb.data.TransactionRepository;
import io.sproutmoney.sproutweb.models.TransactionCategory;
import io.sproutmoney.sproutweb.services.TransactionCategoryService;
import io.sproutmoney.sproutweb.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import retrofit2.Response;

import java.io.IOException;

@Controller
public class PlaidUtilities {

    private Logger logger = LoggerFactory.getLogger(PlaidUtilities.class.getSimpleName());

    private PlaidClient mPlaidClient;

    @Autowired
    TransactionCategoryService mTransactionCategoryService;

    @Autowired
    TransactionRepository mTransactionCategoryRepository;

    //TODO Schedule this to run regularly and sync/normalize categories
    //public boolean syncPlaidTransactionCategories() {
    //    //TODO: Update the environment from sandbox
    //    mPlaidClient = PlaidClient.newBuilder()
    //            .clientIdAndSecret("573b50930259902a3980f121", "b96e031816833914cce7967a2bfce7")
    //            .publicKey("f2ed0e179c86b5a0c0c16dc0bd4dc5").sandboxBaseUrl().build();
//
    //    Response<CategoriesGetResponse> categoriesGetResponse = null;
    //    try {
    //        categoriesGetResponse = mPlaidClient.service()
    //                .categoriesGet(new CategoriesGetRequest())
    //                .execute();
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    if (categoriesGetResponse != null && categoriesGetResponse.isSuccessful()) {
    //        for (CategoriesGetResponse.Category category : categoriesGetResponse.body().getCategories()) {
    //            logger.info(category.getCategoryId() +
    //                    " is " +
    //                    category.getHierarchy().toString() +
    //                    " - " +
    //                    category.getHierarchy().size());
    //            TransactionCategory tc = new TransactionCategory(
    //                    category.getGroup(),
    //                    category.getCategoryId(),
    //                    category.getHierarchy().get(category.getHierarchy().size()-1),
    //                    category.getHierarchy(),
    //                    category.getHierarchy().size() == 1
    //            );
    //            mTransactionCategoryService.saveTransactionCategory(tc);
    //        }
    //        return true;
    //    } else return false;
    //}
}
