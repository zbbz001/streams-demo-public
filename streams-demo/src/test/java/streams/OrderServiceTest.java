package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    OrderService ordersService = new OrderService();
    Order o1;
    Order o2;
    Order o3;

    @BeforeEach
    public void init() {
        Product p1 = new Product("Tv", "IT", 2000);
        Product p2 = new Product("Laptop", "IT", 2400);
        Product p3 = new Product("Phone", "IT", 400);
        Product p4 = new Product("Lord of The Rings", "Book", 20);
        Product p5 = new Product("Harry Potter Collection", "Book", 120);

        o1 = new Order("pending", LocalDate.of(2021, 6, 7));
        o1.addProduct(p1);
        o1.addProduct(p2);
        o1.addProduct(p5);

        o2 = new Order("on delivery", LocalDate.of(2021, 6, 1));
        o2.addProduct(p3);
        o2.addProduct(p1);
        o2.addProduct(p2);

        o3 = new Order("pending", LocalDate.of(2021, 6, 8));
        o3.addProduct(p1);

        ordersService.saveOrder(o1);
        ordersService.saveOrder(o2);
        ordersService.saveOrder(o3);
    }

    @Test
    void testCountOrdersByStatus() {
        assertThat(ordersService.countOrdersByStatus("pending")).isEqualTo(2L);
    }

    @Test
    void testCollectOrdersWithProductCategory() {
        List<Order> filtered = ordersService.collectOrdersWithProductCategory("IT");

        assertThat(filtered).size().isEqualTo(3);

        List<Order> filtered2 = ordersService.collectOrdersWithProductCategory("Book");

        assertThat(filtered2).size().isEqualTo(1);
    }

    @Test
    void testProductsOverAmountPrice() {
        List<Product> filtered = ordersService.productsOverAmountPrice(1000);

        assertThat(filtered).size().isEqualTo(5);
        assertThat(filtered).extracting(Product::getName).contains("Tv");
    }

    @Test
    void testSumPriceIncomeWithinAGivenPeriod() {
        double sumPriceIncome = ordersService.sumPriceIncomeWithinAGivenPeriod(LocalDate.of(2021, 6, 6), LocalDate.of(2021, 6, 9));
        assertThat(sumPriceIncome).isCloseTo(6520.0, within(0.001));
    }

    @Test
    void testSumPriceIncomeWithinAGivenPeriodByWrongPeriod() {
        assertThatThrownBy(() -> {
            ordersService.sumPriceIncomeWithinAGivenPeriod(LocalDate.of(2021, 6, 16), LocalDate.of(2021, 6, 9));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incorrect period");
    }

    @Test
    void testFindProdutInOrders() {
        assertThat(ordersService.findProdutInOrders("Harry Potter Collection").getName()).isEqualTo("Harry Potter Collection");
    }

    @Test
    void testNotFindProductException() {
        assertThatThrownBy(()->{ordersService.findProdutInOrders("Lord of The Rings");})
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");
    }

    @Test
    void testFindOrderwithMostExpensiveProduct() {
        Order orderActual = ordersService.findOrderwithMostExpensiveProduct();

        assertThat(orderActual.getProducts()).extracting(Product::getName).contains("Harry Potter Collection");
        assertThat(orderActual).isEqualTo(o1);
    }

}