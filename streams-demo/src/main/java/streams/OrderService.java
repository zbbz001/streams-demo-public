package streams;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {

    private List<Order> orders = new ArrayList<>();


    public void saveOrder(Order order) {
        orders.add(order);
    }

    public long countOrdersByStatus(String status) {
        return orders.stream()
                .filter(order -> order.getStatus().equals(status))
                .count();
    }

    public List<Order> collectOrdersWithProductCategory(String category) {
        return orders.stream()
                .filter(order -> order.getProducts()
                        .stream().anyMatch(product -> product.getCategory().equals(category)))
                .collect(Collectors.toList());
    }

    public List<Product> productsOverAmountPrice(int amount) {
        return orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .filter(product -> product.getPrice() > amount)
                .collect(Collectors.toList());
    }

    public double sumPriceIncomeWithinAGivenPeriod(LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Incorrect period");
        }

        return orders.stream()
                .filter(order -> order.getOrderDate().isAfter(periodStart) && order.getOrderDate().isBefore(periodEnd))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public Product findProdutInOrders(String productName) {
        return orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .filter(product -> product.getName().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Order findOrderwithMostExpensiveProduct() {
        double maxPrice = orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .max()
                .getAsDouble();

        return orders.stream()
                .filter(order -> order.getProducts().stream().anyMatch(product -> product.getPrice() == maxPrice))
                .findFirst()
                .get();
    }
}
