package com.expensetracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense {
    private static int counter = 1;

    private int id;
    private String description;
    private double amount;
    private String category;
    private LocalDate date;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Expense(String description, double amount, String category, LocalDate date) {
        this.id = counter++;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Constructor for loading from file (id already known)
    public Expense(int id, String description, double amount, String category, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        if (id >= counter) counter = id + 1;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }

    // Convert to CSV line for saving
    public String toCsv() {
        return id + "," + description + "," + amount + "," + category + "," + date.format(FORMATTER);
    }

    // Parse from CSV line
    public static Expense fromCsv(String line) {
        String[] parts = line.split(",", 5);
        int id = Integer.parseInt(parts[0].trim());
        String desc = parts[1].trim();
        double amount = Double.parseDouble(parts[2].trim());
        String category = parts[3].trim();
        LocalDate date = LocalDate.parse(parts[4].trim(), FORMATTER);
        return new Expense(id, desc, amount, category, date);
    }

    @Override
    public String toString() {
        return String.format("[%d] %-25s | %-12s | %-15s | Rs.%.2f",
                id, description, category, date.format(FORMATTER), amount);
    }
}
