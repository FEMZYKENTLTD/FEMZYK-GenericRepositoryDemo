package com.femzyk.genericsdemo.gui;

import java.util.List;

import com.femzyk.genericsdemo.core.Customer;
import com.femzyk.genericsdemo.core.InMemoryRepository;
import com.femzyk.genericsdemo.core.Product;
import com.femzyk.genericsdemo.core.Repository;
import com.femzyk.genericsdemo.core.Result;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * GenericsFxApp - JavaFX front end of the FEMZYK Generics Demo
 * (CS 1103-01, Unit 6 discussion).
 *
 * The window has two tabs backed by the SAME generic repository class:
 *
 *   - Products tab: Repository<Product, Integer> keyed by an int id.
 *   - Customers tab: Repository<Customer, String> keyed by a String id.
 *
 * Each tab shows the live repository content, lets the user add a row,
 * delete the selected row, and look up by id through the generic
 * findById. A generic Result-based status message confirms every action.
 * The point the window makes: the repository class is written once, yet
 * the compiler gives each tab a fully type-safe view of its own type.
 */
public class GenericsFxApp extends Application {

    private static final String APP_TITLE = "FEMZYK Generics Demo - CS 1103-01 Unit 6";

    private final Repository<Product, Integer> productRepository =
            new InMemoryRepository<>(Product::id);
    private final Repository<Customer, String> customerRepository =
            new InMemoryRepository<>(Customer::customerId);

    private TableView<Product> productTable;
    private TableView<Customer> customerTable;
    private Label productStatus;
    private Label customerStatus;
    private TextField productQuantityField;
    private TextField customerIdField;
    private TextField customerNameField;

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_TITLE);
        seed();

        Label title = new Label("FEMZYK Generics Demo");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("CS 1103-01 - Unit 6 - one generic Repository<T, ID>, two type-safe tabs");
        subtitle.getStyleClass().add("header-sub");

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                new Tab("Products - Repository<Product, Integer>", productTab()),
                new Tab("Customers - Repository<Customer, String>", customerTab()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        VBox root = new VBox(14, title, subtitle, tabs);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        refreshProducts();
        refreshCustomers();

        Scene scene = new Scene(root, 820, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /** Builds the products tab: table + add row + lookup. */
    private VBox productTab() {
        productTable = new TableView<>();
        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleObjectProperty<>(String.valueOf(d.getValue().id())));
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        TableColumn<Product, String> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(d -> new SimpleObjectProperty<>(String.valueOf(d.getValue().quantity())));
        productTable.getColumns().setAll(List.of(idCol, nameCol, qtyCol));
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        productTable.setPlaceholder(new Label("Repository is empty."));

        TextField nameField = new TextField();
        nameField.setPromptText("Product name");
        nameField.setId("productNameField");
        productQuantityField = new TextField();
        productQuantityField.setPromptText("Quantity, e.g. 10");
        productQuantityField.setId("productQuantityField");
        Button addButton = new Button("Add product");
        addButton.setId("addProductButton");
        addButton.setOnAction(event -> addProduct(nameField));
        HBox addRow = new HBox(10, nameField, productQuantityField, addButton);
        addRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameField, Priority.ALWAYS);

        Button deleteButton = new Button("Delete selected");
        deleteButton.setId("deleteProductButton");
        deleteButton.setOnAction(event -> deleteSelectedProduct());
        productStatus = statusLabel("productStatus");

        return new VBox(10,
                sectionLabel("Repository<Product, Integer> - int keys"),
                productTable, addRow, deleteButton, productStatus);
    }

    /** Builds the customers tab: table + add row, same repository class. */
    private VBox customerTab() {
        customerTable = new TableView<>();
        TableColumn<Customer, String> idCol = new TableColumn<>("Key (String)");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().customerId()));
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        customerTable.getColumns().setAll(List.of(idCol, nameCol));
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        customerTable.setPlaceholder(new Label("Repository is empty."));

        customerIdField = new TextField();
        customerIdField.setPromptText("Key, e.g. C005");
        customerIdField.setId("customerIdField");
        customerNameField = new TextField();
        customerNameField.setPromptText("Customer name");
        customerNameField.setId("customerNameField");
        Button addButton = new Button("Add customer");
        addButton.setId("addCustomerButton");
        addButton.setOnAction(event -> addCustomer());
        HBox addRow = new HBox(10, customerIdField, customerNameField, addButton);
        addRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(customerNameField, Priority.ALWAYS);

        Button deleteButton = new Button("Delete selected");
        deleteButton.setId("deleteCustomerButton");
        deleteButton.setOnAction(event -> deleteSelectedCustomer());
        customerStatus = statusLabel("customerStatus");

        return new VBox(10,
                sectionLabel("Repository<Customer, String> - String keys, SAME class"),
                customerTable, addRow, deleteButton, customerStatus);
    }

    /** Adds a product through a generic Result-based validation chain. */
    private void addProduct(TextField nameField) {
        hide(productStatus);
        String name = nameField.getText().trim();
        Result<Integer> quantity = parsePositiveInt(productQuantityField.getText());
        if (name.isEmpty()) {
            show(productStatus, "The product name cannot be empty.", true);
            return;
        }
        if (!quantity.isSuccess()) {
            show(productStatus, quantity.error(), true);
            return;
        }
        int newId = nextProductId();
        Product saved = productRepository.save(new Product(newId, name, quantity.value()));
        show(productStatus, String.format("save() accepted %s - repository now holds %d row(s).",
                saved, productRepository.count()), false);
        nameField.clear();
        productQuantityField.clear();
        refreshProducts();
    }

    /** Deletes the selected product. */
    private void deleteSelectedProduct() {
        hide(productStatus);
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            show(productStatus, "Select a row first.", true);
            return;
        }
        boolean removed = productRepository.deleteById(selected.id());
        show(productStatus, removed
                ? "deleteById(" + selected.id() + ") returned true."
                : "deleteById returned false - row was already gone.", removed);
        refreshProducts();
    }

    /** Adds a customer through the String-keyed repository. */
    private void addCustomer() {
        hide(customerStatus);
        String key = customerIdField.getText().trim().toUpperCase();
        String name = customerNameField.getText().trim();
        if (key.isEmpty() || name.isEmpty()) {
            show(customerStatus, "Both the key and the name are required.", true);
            return;
        }
        if (customerRepository.findById(key) != null) {
            show(customerStatus, "Key " + key + " already exists - ids must stay unique.", true);
            return;
        }
        Customer saved = customerRepository.save(new Customer(key, name));
        show(customerStatus, String.format("save() accepted %s - same class, String key.", saved), false);
        customerIdField.clear();
        customerNameField.clear();
        refreshCustomers();
    }

    /** Deletes the selected customer. */
    private void deleteSelectedCustomer() {
        hide(customerStatus);
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            show(customerStatus, "Select a row first.", true);
            return;
        }
        boolean removed = customerRepository.deleteById(selected.customerId());
        show(customerStatus, removed
                ? "deleteById(\"" + selected.customerId() + "\") returned true."
                : "deleteById returned false - row was already gone.", removed);
        refreshCustomers();
    }

    /** Reloads the product table straight from its repository. */
    private void refreshProducts() {
        productTable.getItems().setAll((List<Product>) toList(productRepository.findAll()));
    }

    /** Reloads the customer table straight from its repository. */
    private void refreshCustomers() {
        customerTable.getItems().setAll((List<Customer>) toList(customerRepository.findAll()));
    }

    /** Converts the repository Iterable into a list (no unchecked casts). */
    private <T> List<T> toList(Iterable<T> source) {
        List<T> list = new java.util.ArrayList<>();
        source.forEach(list::add);
        return list;
    }

    /** Parses a positive integer into a Result, never throwing. */
    private Result<Integer> parsePositiveInt(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value < 0) {
                return Result.failure("The quantity cannot be negative.");
            }
            return Result.success(value);
        } catch (NumberFormatException error) {
            return Result.failure("\"" + input.trim() + "\" is not a whole number.");
        }
    }

    /** Returns the next free product id. */
    private int nextProductId() {
        int max = 0;
        for (Product p : productRepository.findAll()) {
            max = Math.max(max, p.id());
        }
        return max + 1;
    }

    /** Seeds both repositories with a few rows. */
    private void seed() {
        productRepository.save(new Product(1, "Wireless Mouse", 40));
        productRepository.save(new Product(2, "Mechanical Keyboard", 15));
        productRepository.save(new Product(3, "USB-C Hub", 30));
        customerRepository.save(new Customer("C001", "Alice Johnson"));
        customerRepository.save(new Customer("C002", "Ben Adams"));
    }

    /** Creates a section heading label. */
    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    /** Creates an initially hidden status label. */
    private Label statusLabel(String id) {
        Label label = new Label();
        label.setId(id);
        label.setWrapText(true);
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    /** Shows a status message in error (red) or success (green) style. */
    private void show(Label label, String message, boolean isError) {
        label.getStyleClass().removeAll("error", "result");
        label.getStyleClass().add(isError ? "error" : "result");
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    /** Hides a status label. */
    private void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
}
