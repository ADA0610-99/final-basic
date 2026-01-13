package ru.otus.java.basic.application;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsStorage {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/otus_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "Zelenodolsk9";
    private final Connection connection;
    private static final String GET_ITEMS = "select i.id, i.items_name, i.price from items i";
    private static final String CREATE_ITEM = "INSERT INTO items (items_name, price)\n" +
            "VALUES (?, ?)";
    private static final String DELETE_ITEM = "delete from items i\n" +
            "  where i.id = ?";
    private static final String GET_ITEMS_FROM_ID = "select i.id, i.items_name, i.price from items i\n" +
            "where i.id = ?";
    private static final String UPDATE_ITEM = "update items i \n" +
            "set items_name = ?, price = ?\n" +
            "where i.id = ?";

    public ItemsStorage() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet res = statement.executeQuery(GET_ITEMS)) {
                while (res.next()) {
                    Long id = res.getLong(1);
                    String title = res.getString(2);
                    int price = res.getInt(3);
                    Item item = new Item(id, title, price);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Collections.unmodifiableList(items);
    }

    public void createItem(Item item) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_ITEM)) {
            statement.setString(1, item.getTitle());
            statement.setInt(2, item.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteItem(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ITEM)) {
            statement.setInt(1, Math.toIntExact(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Item getItems(Long id) {
        Item item = new Item();
        try (PreparedStatement ps = connection.prepareStatement(GET_ITEMS_FROM_ID)) {
            ps.setLong(1, id);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) {
                    item.setId(res.getLong(1));
                    item.setTitle(res.getString(2));
                    item.setPrice(res.getInt(3));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return item;
    }

    public void updateItem(Item item) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ITEM)) {
            ps.setString(1, item.getTitle());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, Math.toIntExact(item.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
