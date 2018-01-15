package io.sproutmoney.sproutweb.models;

//  Created by Justin on 1/14/18

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "transaction_categories")
public class TransactionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_category_id")
    private int id;

    @Column(name = "plaid_group")
    private String plaidGroup;

    @Column(name = "plaid_category_id")
    private String plaidCategoryId;

    @Column(name = "display_name")
    private String displayName;

    @ElementCollection
    @Column(name = "hierarchy")
    private List<String> hierarchy;

    @Column(name = "is_parent")
    private boolean isParent;

    public TransactionCategory() {
    }

    public TransactionCategory(String plaidGroup,
                               String plaidCategoryId,
                               String displayName,
                               List<String> hierarchy,
                               boolean isParent) {
        this.plaidGroup = plaidGroup;
        this.plaidCategoryId = plaidCategoryId;
        this.displayName = displayName;
        this.hierarchy = hierarchy;
        this.isParent = isParent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaidGroup() {
        return plaidGroup;
    }

    public void setPlaidGroup(String plaidGroup) {
        this.plaidGroup = plaidGroup;
    }

    public String getPlaidCategoryId() {
        return plaidCategoryId;
    }

    public void setPlaidCategoryId(String plaidCategoryId) {
        this.plaidCategoryId = plaidCategoryId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(List<String> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }
}
