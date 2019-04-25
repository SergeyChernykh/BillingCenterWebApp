package com.BillingCenter.services;

import com.BillingCenter.dao.*;
import com.BillingCenter.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Services {

    @Autowired
    private CustomerDAO customersDAO;

    @Autowired
    private CustomerInfoDAO customersInfoDAO;

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private PhoneNumbersDAO phoneNumbersDAO;

    @Autowired
    private ServiceDAO serviceDAO;

    @Autowired
    private HistoryDAO historyDAO;

    //клиент
    public Customer getCustomerById(int id) {
        return new CustomerDAO().getCustomerById(id);
    }

    public List<Customer> getAllCustomers(){
        return new CustomerDAO().findAll();
    }

    public void saveCustomer(Customer Customer){
        new CustomerDAO().save(Customer);
    }

    public void updateCustomer(Customer Customer){
        new CustomerDAO().update(Customer);
    }

    public void updateBill(Customer customer, float payment){
        Float bill = customer.getBill();
        if (bill == null) {
            bill = payment;
        } else {
            bill += payment;
        }
        customer.setBill(bill);

        checkCustomerBill(customer);

        Action action = new Action();
        action.setCustomersByCustomerid(customer);
        action.setAction(payment);
        action.setDate(new Timestamp(System.currentTimeMillis()));

        if (customer.getActionsById() == null)
            customer.setActionsById(new ArrayList<Action>());
        customer.getActionsById().add(action);
        new ActionDAO().save(action);
        new CustomerDAO().update(customer);
    }

    public void updateCustomerService(Customer customer, PhoneService service){
        History lastNote = null;
        Date today = new Date();
        List<History> history = customer.getHistoriesById();
        if (history != null && !history.isEmpty()) {
            lastNote = history.get(history.size()-1);
        }
        if (lastNote != null){
            lastNote.setTo(convertUtilToSql(today));
            new HistoryDAO().update(lastNote);
        }

        if (service != null) {
            customer.setServicesByServiceid(service);
            new CustomerDAO().update(customer);

            History newNote = new History();
            newNote.setCustomersByCustomerid(customer);
            newNote.setServicesByServiceid(service);
            newNote.setFrom( convertUtilToSql(today));
            if (customer.getHistoriesById() == null)
                customer.setHistoriesById(new ArrayList<History>());
            customer.getHistoriesById().add(newNote);
            if (service.getHistoriesById() == null)
                service.setHistoriesById(new ArrayList<History>());
            service.getHistoriesById().add(newNote);
            new HistoryDAO().save(newNote);
        }
    }

    private static java.sql.Date convertUtilToSql(Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }
    //!!
    public void updatePhoneNumber(Customer customer, int n){
        for (int i = 0; i < n; ++i){
            PhoneNumber phoneNumber = getFirstFreePhoneNumber();
            phoneNumber.setCustomersByCustomerid(customer);
            phoneNumbersDAO.save(phoneNumber);
            List<PhoneNumber> phoneNumbersENTITIES = customer.getPhoneNumbersById();
            if (phoneNumbersENTITIES == null){
                phoneNumbersENTITIES = new ArrayList<PhoneNumber>();
                customer.setPhoneNumbersById(phoneNumbersENTITIES);
            }
            phoneNumbersENTITIES.add(phoneNumber);
            customersDAO.update(customer);
        }
    }

    public void deleteCustomer(Customer customer){
        customersDAO.delete(customer);
    }

    //котакные лица
    public void checkCustomerBill(Customer customer){
        Float bill = customer.getBill();
        if (bill == null){
            bill = (float)0;
            customer.setBill(bill);
        }
        if (bill > 0){
            customer.setMaxdaytorepayment(null);
            customer.setBlockedflag(false);
        } else {
            Date today = new Date();

            java.sql.Date maxdaytorepayment = customer.getMaxdaytorepayment();
            if (maxdaytorepayment == null){
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                maxdaytorepayment = new java.sql.Date(calendar.getTimeInMillis());
                customer.setMaxdaytorepayment(maxdaytorepayment);
            }

            Float maxDebt = customer.getMaxdebt();
            if (maxDebt == null){
                maxDebt = new Float(0);
                customer.setMaxdebt(maxDebt);
            }

            if (bill <=  maxDebt || today.after(maxdaytorepayment)) {
                customer.setBlockedflag(true);
            }
        }
    }

    public void addNewContact(CustomerInfo contact){
        new CustomerInfoDAO().save(contact);
    }

    public void updateContact(CustomerInfo contact){
        new CustomerInfoDAO().update(contact);
    }

    public void deleteContact(CustomerInfo contact){
        new CustomerInfoDAO().delete(contact);
    }

    //телефонные номера
    public List<PhoneNumber> getAllPhoneNumbers(){
        return new PhoneNumbersDAO().findAll();
    }

    public PhoneNumber getFirstFreePhoneNumber(){
        List<PhoneNumber> numbers = getAllPhoneNumbers();
        PhoneNumber lastNumber = null;

        if (numbers != null && numbers.size() != 0){
            lastNumber = numbers.get(numbers.size()-1);
        }

        int countryCode = 7;
        int regionCode = 999;
        int number;

        if (lastNumber == null){
            number = 10000;
        }else{
            number = lastNumber.getNumber() + 1;
        }
        String fullNumber = "+" + countryCode + "("+ regionCode + ") " +number;

        PhoneNumber newPhoneNumber = new PhoneNumber();
        newPhoneNumber.setCountrycode(countryCode);
        newPhoneNumber.setRegioncode(regionCode);
        newPhoneNumber.setNumber(number);
        newPhoneNumber.setFullnumber(fullNumber);

        return newPhoneNumber;
    }

    //услуга
    public PhoneService getServiceById(int id){
        return new ServiceDAO().getById(id);
    }

    public List<PhoneService> getAllServices(){
        return new ServiceDAO().findAll();
    }

    public void saveService(PhoneService service){
        new ServiceDAO().save(service);
    }

    public void updateService(PhoneService service){
        new ServiceDAO().update(service);
    }

    public void deleteService(PhoneService service) {
        new ServiceDAO().delete(service);
    }
}