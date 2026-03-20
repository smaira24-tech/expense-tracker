package com.expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {

    private List<Expense> expenses = new ArrayList<>();
    private static final String DATA_FILE = "data/expenses.csv";

    public ExpenseManager() {
        loadFromFile();
    }

    // ──────────────────────────────────────────
    // CRUD Operations
    // ──────────────────────────────────────────

    public void addExpense(String description, double amount, String category) {
        Expense e = new Expense(description, amount, category, LocalDate.now());
        expenses.add(e);
        saveToFile();
        System.out.println("\n✅ Expense added: " + e);
    }

    public boolean deleteExpense(int id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) {
            saveToFile();
            System.out.println("🗑️  Expense #" + id + " deleted.");
        } else {
            System.out.println("❌ No expense found with ID " + id);
        }
        return removed;
    }

    public boolean updateExpense(int id, String newDesc, double newAmount, String newCategory) {
        Optional<Expense> found = expenses.stream().filter(e -> e.getId() == id).findFirst();
        if (found.isPresent()) {
            Expense e = found.get();
            if (!newDesc.isEmpty()) e.setDescription(newDesc);
            if (newAmount > 0) e.setAmount(newAmount);
            if (!newCategory.isEmpty()) e.setCategory(newCategory);
            saveToFile();
            System.out.println("✏️  Updated: " + e);
            return true;
        }
        System.out.println("❌ No expense found with ID " + id);
        return false;
    }

    // ──────────────────────────────────────────
    // Listing & Filtering
    // ──────────────────────────────────────────

    public void listAll() {
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses recorded yet.");
            return;
        }
        printHeader();
        expenses.forEach(System.out::println);
        printFooter(expenses);
    }

    public void listByCategory(String category) {
        List<Expense> filtered = expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            System.out.println("📭 No expenses in category: " + category);
            return;
        }
        printHeader();
        filtered.forEach(System.out::println);
        printFooter(filtered);
    }

    public void listByMonth(int month, int year) {
        List<Expense> filtered = expenses.stream()
                .filter(e -> e.getDate().getMonthValue() == month && e.getDate().getYear() == year)
                .collect(Collectors.toList());
        String monthName = Month.of(month).name();
        if (filtered.isEmpty()) {
            System.out.println("📭 No expenses in " + monthName + " " + year);
            return;
        }
        System.out.println("\n📅 Expenses for " + monthName + " " + year + ":");
        printHeader();
        filtered.forEach(System.out::println);
        printFooter(filtered);
    }

    // ──────────────────────────────────────────
    // Summary & Analytics
    // ──────────────────────────────────────────

    public void showSummary() {
        if (expenses.isEmpty()) {
            System.out.println("📭 No data to summarize.");
            return;
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double avg   = expenses.stream().mapToDouble(Expense::getAmount).average().orElse(0);
        Expense max  = expenses.stream().max(Comparator.comparingDouble(Expense::getAmount)).get();
        Expense min  = expenses.stream().min(Comparator.comparingDouble(Expense::getAmount)).get();

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║          EXPENSE SUMMARY             ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Total Expenses  : Rs. %-13.2f║%n", total);
        System.out.printf( "║  Average         : Rs. %-13.2f║%n", avg);
        System.out.printf( "║  Highest         : Rs. %-13.2f║%n", max.getAmount());
        System.out.printf( "║  Lowest          : Rs. %-13.2f║%n", min.getAmount());
        System.out.printf( "║  Total Records   : %-16d║%n", expenses.size());
        System.out.println("╚══════════════════════════════════════╝");

        System.out.println("\n📊 Spending by Category:");
        System.out.println("─".repeat(40));
        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
        byCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    int bar = (int) (entry.getValue() / total * 30);
                    System.out.printf("  %-12s | %s Rs.%.2f%n",
                            entry.getKey(),
                            "█".repeat(bar),
                            entry.getValue());
                });
    }

    public void showMonthlyReport() {
        if (expenses.isEmpty()) { System.out.println("📭 No data."); return; }

        System.out.println("\n📅 Monthly Spending Report:");
        System.out.println("─".repeat(40));
        Map<String, Double> byMonth = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue()),
                        TreeMap::new,
                        Collectors.summingDouble(Expense::getAmount)));
        byMonth.forEach((month, total) ->
                System.out.printf("  %s  |  Rs. %.2f%n", month, total));
    }

    // ──────────────────────────────────────────
    // File I/O
    // ──────────────────────────────────────────

    private void saveToFile() {
        try {
            new File("data").mkdirs();
            PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE));
            for (Expense e : expenses) {
                pw.println(e.toCsv());
            }
            pw.close();
        } catch (IOException ex) {
            System.out.println("⚠️  Could not save: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    expenses.add(Expense.fromCsv(line));
                }
            }
        } catch (IOException ex) {
            System.out.println("⚠️  Could not load data: " + ex.getMessage());
        }
    }

    // ──────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────

    private void printHeader() {
        System.out.println("\n" + "─".repeat(72));
        System.out.printf("%-4s %-25s %-13s %-16s %s%n", "ID", "Description", "Category", "Date", "Amount");
        System.out.println("─".repeat(72));
    }

    private void printFooter(List<Expense> list) {
        double total = list.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println("─".repeat(72));
        System.out.printf("  Total: Rs. %.2f  (%d expense%s)%n", total, list.size(), list.size() == 1 ? "" : "s");
    }

    public List<String> getCategories() {
        return expenses.stream()
                .map(Expense::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
