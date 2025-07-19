package dto;

import dbenum.AccountType;

public class AccountDTO {
    private int accountID;
    private int customerID;
    private String customerName;
    private AccountType accountType;
    private String accNo;
    private boolean isInterestEligible;
    private boolean isActive;

    public AccountDTO(int accountID, String accNo, int cusID){
        this.accountID = accountID;
        this.accNo = accNo;
        this.customerID = cusID;
    }


    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public boolean isInterestEligible() {
        return isInterestEligible;
    }

    public void setInterestEligible(boolean interestEligible) {
        isInterestEligible = interestEligible;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
