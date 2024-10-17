package controller.order;

import controller.cusromer.CustomerController;
import controller.item.ItemController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFromController implements Initializable {

    @FXML
    public ComboBox<String> cmbItemCode;

    @FXML
    public Label lblNetTotal;

    @FXML
    public TextField txtOrderID;

    @FXML
    private ComboBox<String> cmbCustomerId;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblTime;

    @FXML
    private TableView<CartTM> tblCart;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtItemDescription;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtUnitPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        loadCustomerIds();
        loadItemIds();
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newVal) -> {
            if(newVal!=null){
                searchCustomer(newVal);
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newVal) -> {
            if(newVal!=null){
                searchItem(newVal);
            }
        });
    }

    ObservableList<CartTM> cartTMS = FXCollections.observableArrayList();

    public void btnAddToCartOnAction(ActionEvent actionEvent) {

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        String itemCode = cmbItemCode.getValue();
        String description = txtItemDescription.getText();
        Integer qty = Integer.parseInt(txtQty.getText());
        Double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        Double total = unitPrice*qty;

        if(Integer.parseInt(txtStock.getText())<=qty){
            new Alert(Alert.AlertType.WARNING, "Stock Quantity Not Enough").show();
        }else{
            cartTMS.add(new CartTM(itemCode,description,qty,unitPrice,total));
        }

        tblCart.setItems(cartTMS);

        calNetTotal();
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        String orderId = txtOrderID.getText();
        LocalDate orderDate = LocalDate.now();
        String customerId = cmbCustomerId.getValue();

        List<OrderDetail> orderDetails = new ArrayList<>();

        cartTMS.forEach(obj->{
            orderDetails.add(new OrderDetail(orderId, obj.getItemCode(), obj.getQty(),0.0));
        });

        Order order = new Order(orderId, orderDate, customerId, orderDetails);

        new OrderController().placeOrder(order);

        System.out.println(order);
    }

    private void loadDateAndTime(){

        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = f.format(date);
        lblDate.setText(dateNow);

        //----------- Time ---------------------------

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,e->{
            LocalTime now = LocalTime.now();
            lblTime.setText(now.getHour()+" : "+now.getMinute()+" : "+now.getSecond());
        }),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void loadCustomerIds(){
        ObservableList<String> customerIds = CustomerController.getInstance().getCustomerIds();

        cmbCustomerId.setItems(customerIds);
    }

    private void searchCustomer(String customerId){

       Customer customer = CustomerController.getInstance().searchCustomer(customerId);
       txtCustomerName.setText(customer.getName());
       txtCustomerAddress.setText(customer.getAddress());

    }

    private void loadItemIds(){
        ObservableList<String> ItemIds = ItemController.getInstance().getItemIds();

        cmbItemCode.setItems(ItemIds);
    }

    private void searchItem(String itemCode){

        Item item = ItemController.getInstance().searchItem(itemCode);
        txtItemDescription.setText(item.getDescription());
        txtStock.setText(item.getQty().toString());
        txtUnitPrice.setText(item.getUnitPrice().toString());

    }

    private void calNetTotal(){
        Double total=0.0;

        for(CartTM cartTM: cartTMS){
            total+= cartTM.getTotal();
        }

        lblNetTotal.setText(total.toString());
    }
}
