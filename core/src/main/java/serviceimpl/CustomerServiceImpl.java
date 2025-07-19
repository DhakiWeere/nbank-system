package serviceimpl;

import annotation.SanitizeInput;
import annotation.ServiceCallLog;
import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import dbenum.LoginRole;
import dbenum.TransactionType;
import dto.AccountDTO;
import entity.Account;
import entity.Customer;
import entity.LoginCredentials;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import service.CustomerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@ServiceCallLog
public class CustomerServiceImpl implements CustomerService {

    @Inject
    CustomerAccountDAO customerAccountDAO;

    @Inject
    TransactionDAO transactionDAO;

    public String getCustomerNameByNIC(String nic) {
        System.out.println("getCustomerNameByNIC");
        Customer customer = customerAccountDAO.getCustomerByNIC(nic);
        return customer.getNic() + " " + customer.getFullName() + " " + customer.getEmail();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerCustomer(String nic, String email, String fullName, String phone, String address, String password) {
        try {
            if (customerAccountDAO.getCustomerByNIC(nic) == null) {
                System.out.println("Customer Already Registered");

            } else {
                Customer newCustomer = customerAccountDAO.addCustomer(nic, email, fullName, phone, address);
                customerAccountDAO.addLoginCredentials(fullName, password, LoginRole.CUSTOMER, newCustomer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @SanitizeInput
    public boolean authCustomerLoginCredentials(String username, String password) {
        LoginCredentials creds = customerAccountDAO.getLoginCredentialsByUsername(username);
        if (creds != null) {
            if (creds.getUsername().equals(username) && creds.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AccountDTO> getAccountIdsByCustomerID(int customerID) {
        Customer customer = customerAccountDAO.getCustomerById(customerID);
        List<Account> accList = customerAccountDAO.getAccountsByCustomer(customer);

        List<AccountDTO> tempAccountDTOList = new ArrayList<>();
        for (Account acc : accList) {
            AccountDTO tempDTO = new AccountDTO(acc.getId(), acc.getAccNo(), acc.getCustomer().getId());
            tempDTO.setCustomerName(acc.getCustomer().getFullName());
            tempDTO.setAccountType(acc.getType());
            tempDTO.setInterestEligible(acc.getIsInterestEligible());

            tempAccountDTOList.add(tempDTO);
        }

        return tempAccountDTOList;
    }


}
