package controller.item;

import controller.cusromer.CustomerController;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.Item;
import model.OrderDetail;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemController implements ItemService {

    private static ItemController instance;

    private ItemController(){

    }

    public static ItemController getInstance() {
        return instance==null?instance=new ItemController():instance;
    }

    @Override
    public boolean addItem(Item item) {
        String SQl = "INSERT INTO Item VALUES(?,?,?,?,?)";
        try {
            return CrudUtil.execute(
                    SQl,
                    item.getItemCode(),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getUnitPrice(),
                    item.getQty()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean deleteItem(String id) {
        try {
            return CrudUtil.execute("DELETE  FROM item WHERE itemCode=?", id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ObservableList<Item> getAll() {
        String SQl = "SELECT * FROM item";
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = CrudUtil.execute(SQl);

            while (resultSet.next()) {
                itemObservableList.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                ));
            }
            return itemObservableList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateUpdate(Item item) {
        String SQL = "UPDATE item SET Description=?, PackSize=?, UnitPrice=?, QtyOnHand=? WHERE ItemCode=?";
        try {
            return CrudUtil.execute(
                    SQL,
                    item.getDescription(),
                    item.getPackSize(),
                    item.getUnitPrice(),
                    item.getQty(),
                    item.getItemCode()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item searchItem(String itemCode) {
        String SQl = "SELECT * FROM item WHERE itemCode=?";
        try {
            ResultSet resultSet = CrudUtil.execute(SQl, itemCode);
            while (resultSet.next()) {
                return new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Customer searchCustomer(String id) {
        String SQL = "SELECT * FROM customer WHERE CustID=?";

        try {
            ResultSet resultSet = CrudUtil.execute(SQL, id);

            if (resultSet.next()) { // Use `if` since there should be only one result for a unique ID
                return new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null only if no customer is found
    }


    @Override
    public ObservableList<String> getItemIds(){
        ObservableList<String> ItemIds = FXCollections.observableArrayList();
        ObservableList<Item> itemObservableList = getAll();

        itemObservableList.forEach(customer -> {
            ItemIds.add(customer.getItemCode());
        });
        return ItemIds;
    }

    @Override
    public boolean updateStock(List<OrderDetail> orderDetails) {

        for (OrderDetail orderDetail: orderDetails){
            boolean updateS = updateStock(orderDetail);
            if(!updateS){
                return false;
            }
        }
        return true;
    }

    public boolean updateStock(OrderDetail orderDetails){
        String SQL = "UPDATE Item SET QtyOnHand=QtyOnHand-? WHERE ItemCode=?";
        try {
           return CrudUtil.execute(SQL,orderDetails.getQty(),orderDetails.getItemCode());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
