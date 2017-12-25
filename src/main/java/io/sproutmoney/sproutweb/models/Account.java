package io.sproutmoney.sproutweb.models;

//  Created by Justin on 12/18/17

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plaid_account_id")
    @NotEmpty(message = "Plaid account number is required")
    private String plaidAccountId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_subtype")
    private String accountSubtype;

    @Column(name = "institution_id")
    private String institutionId;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "current_balance")
    private double currentBalance;

    @Column(name = "available_balance")
    private double availableBalance;

    @Column(name = "account_limit")
    private double accountLimit;

    @Column(name = "name")
    private String name;

    @Column(name = "official_name")
    private String officialName;

    @Column(name = "mask")
    private String mask;

    @ManyToOne
    @JoinColumn(name = "plaid_item_id", nullable = false)
    private PlaidItem plaidItem;

    public Account() {
    }

    //TODO: Need to figure out how to fetch account subtype
    //TODO: Need to figure out how to fetch official name
    //TODO: Need to fetch and sync mask
    public Account(User user, String plaidAccountId,
                   String accountType, String institutionId,
                   String name, PlaidItem plaidItem) {
        this.user = user;
        this.plaidAccountId = plaidAccountId;
        this.accountType = accountType;
        this.institutionId = institutionId;
        this.name = name;
        this.plaidItem = plaidItem;
        this.institutionName = plaidItem.getInsitutionName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlaidAccountId() {
        return plaidAccountId;
    }

    public void setPlaidAccountId(String plaidAccountId) {
        this.plaidAccountId = plaidAccountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountSubtype() {
        return accountSubtype;
    }

    public void setAccountSubtype(String accountSubtype) {
        this.accountSubtype = accountSubtype;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public double getCurrentBalance() {
        if (this.accountType.equals("credit")) return currentBalance * -1.0;
        else return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public double getAccountLimit() {
        return accountLimit;
    }

    public void setAccountLimit(double accountLimit) {
        this.accountLimit = accountLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public PlaidItem getPlaidItem() {
        return plaidItem;
    }

    public void setPlaidItem(PlaidItem plaidItem) {
        this.plaidItem = plaidItem;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String insitutionName) {
        this.institutionName = insitutionName;
    }
}
