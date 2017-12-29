package io.sproutmoney.sproutweb.models;

//  Created by Justin on 12/26/17

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer id;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    /*
    Relationship IDs (user, account (sprout internal))
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    //TODO: Determine if internal sprout transaction categories make sense as a model/column

    @Column(name = "plaid_account_id")
    private String plaidAccountId;

    @Column(name = "plaid_transaction_id")
    private String plaidTransactionId;

    @Column(name = "plaid_transaction_type")
    private String plaidTransactionType;

    @Column(name = "plaid_category_id")
    private String plaidCategoryId;

    @Column(name = "plaid_category")
    @ElementCollection
    private List<String> plaidCategory;

    @Column(name = "amount")
    private double amount;

    @Column(name = "date")
    private Date date;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "pending")
    private boolean pending;

    @Column(name = "location_street_address")
    private String locationStreetAddress;

    @Column(name = "location_city")
    private String locationCity;

    @Column(name = "location_state")
    private String locationState;

    @Column(name = "location_zip")
    private String locationZip;

    //TODO: Convert string date value into Date for storing and operating (make a utility?)

    public Transaction(User user, Account account, String merchantName,
                       List<String> plaidCategory, String plaidCategoryId,
                       String plaidAccountId, String plaidTransactionId,
                       String plaidTransactionType, double amount, String date,
                       String locationStreetAddress, String locationCity,
                       String locationState, String locationZip, boolean pending) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        this.user = user;
        this.account = account;
        this.merchantName = merchantName;
        this.plaidCategory = plaidCategory;
        this.plaidCategoryId = plaidCategoryId;
        this.plaidAccountId = plaidAccountId;
        this.plaidTransactionId = plaidTransactionId;
        this.plaidTransactionType = plaidTransactionType;
        this.amount = amount;
        this.locationStreetAddress = locationStreetAddress;
        this.locationCity = locationCity;
        this.locationState = locationState;
        this.locationZip = locationZip;
        // Parse the string to a date and save it on the object
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Transaction() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getPlaidAccountId() {
        return plaidAccountId;
    }

    public void setPlaidAccountId(String plaidAccountId) {
        this.plaidAccountId = plaidAccountId;
    }

    public String getPlaidTransactionId() {
        return plaidTransactionId;
    }

    public void setPlaidTransactionId(String plaidTransactionId) {
        this.plaidTransactionId = plaidTransactionId;
    }

    public String getPlaidTransactionType() {
        return plaidTransactionType;
    }

    public void setPlaidTransactionType(String plaidTransactionType) {
        this.plaidTransactionType = plaidTransactionType;
    }

    public String getPlaidCategoryId() {
        return plaidCategoryId;
    }

    public void setPlaidCategoryId(String plaidCategoryId) {
        this.plaidCategoryId = plaidCategoryId;
    }

    public List<String> getPlaidCategory() {
        return plaidCategory;
    }

    public void setPlaidCategory(List<String> plaidCategory) {
        this.plaidCategory = plaidCategory;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getLocationStreetAddress() {
        return locationStreetAddress;
    }

    public void setLocationStreetAddress(String locationStreetAddress) {
        this.locationStreetAddress = locationStreetAddress;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getLocationZip() {
        return locationZip;
    }

    public void setLocationZip(String locationZip) {
        this.locationZip = locationZip;
    }
}
