package com.example.demo.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Bus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    // Establish JDBC Connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    // Landing / Home page - simple welcome
    @GetMapping("/")
    public String landing(Model model) {
        // optional: quick statistics for welcome page
        List<Bus> buses = getAllBuses();
        model.addAttribute("totalBuses", buses.size());
        return "home"; // home.html (create this file in templates)
    }

    // List all buses (moved from "/")
    @GetMapping("/buses")
    public String listBuses(Model model) {
        List<Bus> buses = getAllBuses();
        model.addAttribute("buses", buses);
        return "index"; // index.html
    }

    // Fetch all buses from the database
    private List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM bus";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Bus bus = new Bus();
                bus.setId(resultSet.getInt("id"));
                bus.setBusNumber(resultSet.getString("bus_number"));
                bus.setSource(resultSet.getString("source"));
                bus.setDestination(resultSet.getString("destination"));
                bus.setSeatType(resultSet.getString("seat_type"));
                bus.setAvailableSeats(resultSet.getInt("available_seats"));
                bus.setTotalSeats(resultSet.getInt("total_seats"));
                bus.setTicketPrice(resultSet.getDouble("ticket_price"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            logger.error("Error fetching buses: ", e);
        }
        return buses;
    }

    // Show Add Bus Form
    @GetMapping("/add")
    public String showAddBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        return "add-bus"; // add-bus.html
    }

    // Add a New Bus
    @PostMapping("/add")
    public String addBus(@ModelAttribute Bus bus, RedirectAttributes redirectAttributes, Model model) {
        String sql = "INSERT INTO bus (bus_number, source, destination, seat_type, available_seats, total_seats, ticket_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bus.getBusNumber());
            stmt.setString(2, bus.getSource());
            stmt.setString(3, bus.getDestination());
            stmt.setString(4, bus.getSeatType());
            stmt.setInt(5, bus.getAvailableSeats());
            stmt.setInt(6, bus.getTotalSeats());
            stmt.setDouble(7, bus.getTicketPrice());

            stmt.executeUpdate();
            redirectAttributes.addFlashAttribute("success", "Bus added successfully!");
            return "redirect:/buses"; // redirect to the buses list
        } catch (SQLException e) {
            logger.error("Error adding bus: ", e);
            model.addAttribute("error", "Error adding bus: " + e.getMessage());
            return "add-bus";
        }
    }

    // Search buses by source and destination
    @GetMapping("/search")
    public String searchBuses(@RequestParam(required = false, defaultValue = "") String source,
                              @RequestParam(required = false, defaultValue = "") String destination,
                              Model model) {
        List<Bus> buses = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM bus WHERE source LIKE ? AND destination LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + source + "%");
            statement.setString(2, "%" + destination + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Bus bus = new Bus();
                bus.setId(resultSet.getInt("id"));
                bus.setBusNumber(resultSet.getString("bus_number"));
                bus.setSource(resultSet.getString("source"));
                bus.setDestination(resultSet.getString("destination"));
                bus.setSeatType(resultSet.getString("seat_type"));
                bus.setAvailableSeats(resultSet.getInt("available_seats"));
                bus.setTotalSeats(resultSet.getInt("total_seats"));
                bus.setTicketPrice(resultSet.getDouble("ticket_price"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            logger.error("Error searching buses: ", e);
        }
        model.addAttribute("buses", buses);
        return "search-bus"; // search-bus.html
    }

    // Show Edit Bus Form
    @GetMapping("/edit/{id}")
    public String showEditBusForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Bus bus = getBusById(id);
            model.addAttribute("bus", bus);
            return "edit-bus"; // edit-bus.html
        } catch (Exception e) {
            logger.error("Error fetching bus for edit: ", e);
            redirectAttributes.addFlashAttribute("error", "Error fetching bus for edit: " + e.getMessage());
            return "redirect:/buses";
        }
    }

    // Fetch bus by ID
    private Bus getBusById(int id) throws Exception {
        Bus bus = new Bus();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM bus WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                bus.setId(resultSet.getInt("id"));
                bus.setBusNumber(resultSet.getString("bus_number"));
                bus.setSource(resultSet.getString("source"));
                bus.setDestination(resultSet.getString("destination"));
                bus.setSeatType(resultSet.getString("seat_type"));
                bus.setAvailableSeats(resultSet.getInt("available_seats"));
                bus.setTotalSeats(resultSet.getInt("total_seats"));
                bus.setTicketPrice(resultSet.getDouble("ticket_price"));
            } else {
                throw new Exception("Bus not found");
            }
        } catch (SQLException e) {
            logger.error("Error fetching bus by ID: ", e);
            throw new Exception("Error fetching bus by ID: " + e.getMessage());
        }
        return bus;
    }

    // Update Bus
    @PostMapping("/edit/{id}")
    public String editBus(@PathVariable int id, @ModelAttribute Bus bus, RedirectAttributes redirectAttributes, Model model) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE bus SET bus_number = ?, source = ?, destination = ?, seat_type = ?, available_seats = ?, total_seats = ?, ticket_price = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, bus.getBusNumber());
            statement.setString(2, bus.getSource());
            statement.setString(3, bus.getDestination());
            statement.setString(4, bus.getSeatType());
            statement.setInt(5, bus.getAvailableSeats());
            statement.setInt(6, bus.getTotalSeats());
            statement.setDouble(7, bus.getTicketPrice());
            statement.setInt(8, id);
            statement.executeUpdate();

            redirectAttributes.addFlashAttribute("success", "Bus updated successfully!");
            return "redirect:/buses";
        } catch (SQLException e) {
            logger.error("Error updating bus: ", e);
            model.addAttribute("error", "Error updating bus: " + e.getMessage());
            return "edit-bus";
        }
    }

    // Delete Bus
    @PostMapping("/delete/{id}")
    public String deleteBus(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM bus WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            redirectAttributes.addFlashAttribute("success", "Bus deleted successfully!");
            return "redirect:/buses";
        } catch (SQLException e) {
            logger.error("Error deleting bus: ", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting bus.");
            return "redirect:/buses";
        }
    }
}
