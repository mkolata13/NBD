import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import model.*;

import java.util.List;

public class App {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("POSTGRES_SHOP");
            EntityManager em = emf.createEntityManager();
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Shop shop = new Shop(em, validator);
            shop.getClientManager().addClient("Adam", "Malysz", "393958102",  new ClientTypeSilver());
            shop.getClientManager().addClient("Jan", "Bednarek", "534253454", new ClientTypeGold());

            List<Client> clients = shop.getClientManager().getAllClients();
            Long idKlient1 = clients.get(0).getId();
            Long idKlient2 = clients.get(1).getId();

            shop.getProductManager().addProduct("jablko", 10, 1, 2, "abc");
            shop.getProductManager().addProduct("gruszka", 50, 3, 2, "abc");
            shop.getProductManager().addProduct("banan", 20, 1, 2, "abc");

            List<Product> products = shop.getProductManager().getAllProducts();
            Long id1product = products.get(0).getId();
            Long id2product = products.get(1).getId();
            Long id3product = products.get(2).getId();

            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient2), shop.getProductManager().getAllProducts());
            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient1), shop.getProductManager().getProduct(id1product));
            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient1), shop.getProductManager().getProduct(id1product));
            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient2), shop.getProductManager().getProduct(id2product));
            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient1), shop.getProductManager().getProduct(id2product));
            shop.getCustomerOrderManager().addCustomerOrder(shop.getClientManager().getClient(idKlient1), shop.getProductManager().getProduct(id3product));
        }
    }
}
