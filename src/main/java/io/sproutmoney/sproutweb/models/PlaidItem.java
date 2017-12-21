package io.sproutmoney.sproutweb.models;

//  Created by Justin on 12/16/17

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "plaid_items")
public class PlaidItem {

    @Id
    @Column(name = "plaid_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "external_plaid_item_id")
    private String externalPlaidItemId;

    @Column(name = "access_token")
    private String accessToken;

    // Related user (owner)
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "institution_name")
    private String insitutionName;

    @Column(name = "institution_id")
    private String institutionId;

    @OneToMany(mappedBy = "plaidItem", cascade = CascadeType.ALL)
    private Set<Account> accounts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExternalPlaidItemId() {
        return externalPlaidItemId;
    }

    public void setExternalPlaidItemId(String externalPlaidItemId) {
        this.externalPlaidItemId = externalPlaidItemId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInsitutionName() {
        return insitutionName;
    }

    public void setInsitutionName(String insitutionName) {
        this.insitutionName = insitutionName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }
}
