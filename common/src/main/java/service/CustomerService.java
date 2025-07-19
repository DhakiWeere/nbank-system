package service;

import dto.AccountDTO;
import jakarta.ejb.Local;

import java.util.List;
import java.util.Map;

@Local
public interface CustomerService {
    public String getCustomerNameByNIC(String nic);

    public void registerCustomer(String nic, String email, String fullName, String phone, String address, String password);

    public boolean authCustomerLoginCredentials(String username, String password);

    public List<AccountDTO> getAccountIdsByCustomerID(int customerID);
}
