package serviceimpl;

import annotation.ServiceCallLog;
import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import dbenum.AccountType;
import entity.Account;
import entity.Customer;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import service.AccountService;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@ServiceCallLog
public class AccountServiceImpl implements AccountService {

    @Inject
    TransactionDAO transactionDAO;

    @Inject
    CustomerAccountDAO customerAccountDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createAccountForNIC(String nic, AccountType accType) {
        customerAccountDAO.addAccount(customerAccountDAO.getCustomerByNIC(nic), accType);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void markAccountInactive(String accountNo) {
        customerAccountDAO.updateAccountActive(false, accountNo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void markAccountActive(String accountNo) {
        customerAccountDAO.updateAccountActive(true, accountNo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean isAccountActive(String accountNo) {
        return customerAccountDAO.getAccountIsActive(accountNo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Double getBalanceByAccNo(String accountNo) {
        Account account = customerAccountDAO.getAccountByAccNo(accountNo);
        return customerAccountDAO.getBalanceByAccID(account.getId());
    }


}
