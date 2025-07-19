package dao;

import dbenum.AccountType;
import dbenum.LoginRole;
import entity.Account;
import entity.Balance;
import entity.Customer;
import entity.LoginCredentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.util.List;

@RequestScoped
public class CustomerAccountDAO {

    @PersistenceContext(name = "NBPers")
    private EntityManager em;

    public Customer getCustomerByNIC(String nic){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        query.select(root).where(cb.equal(root.get("nic"), nic));

        TypedQuery<Customer> typedQuery = em.createQuery(query);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }

    public Customer getCustomerById(int customerID){
        return em.find(Customer.class, customerID);
    }

    public LoginCredentials getCustomerCredential(Customer customer){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LoginCredentials> query = cb.createQuery(LoginCredentials.class);
        Root<LoginCredentials> root = query.from(LoginCredentials.class);
        query.select(root).where(cb.equal(root.get("customer"), customer));

        TypedQuery<LoginCredentials> typedQuery = em.createQuery(query);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }

    public List<Account> getAccountsByCustomer(Customer customer) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);
        query.select(root).where(cb.equal(root.get("customer"), customer));

        TypedQuery<Account> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public Account getAccountByAccNo(String accNo){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);
        query.select(root).where(cb.equal(root.get("accNo"), accNo));

        return em.createQuery(query).getSingleResult();
    }

    public Customer addCustomer(String nic, String email, String fullName, String phone, String address){
        if(getCustomerByNIC(nic) != null){
            return null;

        } else {
            Customer newCustomer = new Customer();
            newCustomer.setNic(nic);
            newCustomer.setEmail(email);
            newCustomer.setFullName(fullName);
            newCustomer.setPhone(phone);
            newCustomer.setAddress(address);
            em.persist(newCustomer);

            return newCustomer;
        }
    }

    private static String generateAccountNumber() {
        // new number between 1000000000 (inclusive) and 9999999999 (inclusive)
        long number = 1_000_000_000L + (long)(new SecureRandom().nextDouble() * 9_000_000_000L);
        return String.valueOf(number);
    }

    public Account addAccount(Customer customer, AccountType accountType){
        Account newAccount =  new Account();
        newAccount.setCustomer(customer);
        newAccount.setType(accountType);

        // account no gen
        for(int i=0; i<5; i++) {
            String tempNewAccNo = generateAccountNumber();
            List<Account> resultList = em.createQuery("SELECT a FROM Account a WHERE a.accNo = :accNo", Account.class)
                    .setParameter("accNo", tempNewAccNo)
                    .getResultList();

            if (resultList.isEmpty()) {
                newAccount.setAccNo(tempNewAccNo);
                em.persist(newAccount);
                break;
            }
        }

        return newAccount;
    }

    public void updateAccountActive(boolean activeStatus, String accountNo){
        Account account = getAccountByAccNo(accountNo);
        account.setActive(activeStatus);
        em.merge(account);
    }

    public boolean getAccountIsActive(String accountNo){
        Account account = getAccountByAccNo(accountNo);
        return account.isActive();
    }

    public LoginCredentials addLoginCredentials(String username, String password, LoginRole logRole, Customer customer){
        LoginCredentials newLogCreds = new LoginCredentials();
        newLogCreds.setUsername(username);
        newLogCreds.setPassword(password);
        newLogCreds.setRole(logRole);
        newLogCreds.setCustomer(customer);

        em.persist(newLogCreds);
        return newLogCreds;
    }

    public LoginCredentials getLoginCredentialsByUsername(String username){
        TypedQuery<LoginCredentials> query = em.createQuery("SELECT l FROM LoginCredentials l WHERE l.username = :username",  LoginCredentials.class)
                .setParameter("username", username);
        return query.getResultStream().findFirst().orElse(null);
    }

    public Double getBalanceByAccID(int accID){
        Balance balance = em.find(Balance.class, accID);
        return balance.getBalance().doubleValue();
    }

    public List<Account> getAccountsByInterestEnabled(){
        return em.createQuery("SELECT a FROM Account a WHERE a.interestEligible = true", Account.class).getResultList();
    }
}
