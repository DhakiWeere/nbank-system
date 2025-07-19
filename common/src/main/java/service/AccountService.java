package service;

import dbenum.AccountType;

public interface AccountService {
    public void createAccountForNIC(String nic, AccountType accountType);

    public void markAccountInactive(String accountNo);

    public void markAccountActive(String accountNo);

    public boolean isAccountActive(String accountNo);

    public Double getBalanceByAccNo(String accountNo);
}
