package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private String orderId;
    private LocalDate orderDate;
    private String customerId;

    List<OrderDetail> orderDetails;
}
