package model;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import managers.ClientManager;
import managers.CustomerOrderManager;
import managers.ProductManager;

public class Shop {
    private final ClientManager clientManager;
    private final ProductManager productManager;
    private final CustomerOrderManager customerOrderManager;

    public Shop(EntityManager em, Validator validator) {
        clientManager = new ClientManager(em, validator);
        productManager = new ProductManager(em, validator);
        customerOrderManager = new CustomerOrderManager(em);
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public CustomerOrderManager getCustomerOrderManager() {
        return customerOrderManager;
    }
}
