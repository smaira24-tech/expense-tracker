package com.expensetracker;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ExpenseManager manager = new ExpenseManager();

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1" -> addExpense();
                case "2" -> manager.listAll();
                case "3" -> filterByCategory();
                case "4" -> filterByMonth();
                case "5" -> updateExpense();
                case "6" -> deleteExpense();
                case "7" -> manager.showSummary();
                case "8" -> manager.showMonthlyReport();
                case "9" -> { System.out.println("👋 Goodbye! Keep tracking your expenses."); running = false; }
                default  -> System.out.println("❌ Invalid choice. Enter 1–9.");
            }
        }
        sc.close();
    }

    // ──────────────────────────────────────────
    // Menu Actions
    // ──────────────────────────────────────────

    static void addExpense() {
        System.out.print("📝 Description: ");
        String desc = sc.nextLine().trim();
        if (desc.isEmpty()) { System.out.println("❌ Description cannot be empty."); return; }

        double amount = 0;
        while (amount <= 0) {
            System.out.print("💰 Amount (Rs.): ");
            try { amount = Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("❌ Enter a valid number."); }
        }

        System.out.println("📂 Categories: Food, Transport, Shopping, Bills, Health, Entertainment, Other");
        System.out.print("📂 Category: ");
        String category = sc.nextLine().trim();
        if (category.isEmpty()) category = "Other";

        manager.addExpense(desc, amount, category);
    }

    static void filterByCategory() {
        List<String> cats = manager.getCategories();
        if (cats.isEmpty()) { System.out.println("📭 No categories yet."); return; }
        System.out.println("Available: " + String.join(", ", cats));
        System.out.print("🔍 Enter category: ");
        manager.listByCategory(sc.nextLine().trim());
    }

    static void filterByMonth() {
        int month = 0, year = 0;
        while (month < 1 || month > 12) {
            System.out.print("📅 Month (1–12): ");
            try { month = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("❌ Enter a valid month."); }
        }
        while (year < 2000) {
            System.out.print("📅 Year (e.g. 2024): ");
            try { year = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("❌ Enter a valid year."); }
        }
        manager.listByMonth(month, year);
    }

    static void updateExpense() {
        System.out.print("✏️  Enter Expense ID to update: ");
        int id;
        try { id = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("❌ Invalid ID."); return; }

        System.out.print("New description (press Enter to skip): ");
        String desc = sc.nextLine().trim();

        double amount = -1;
        System.out.print("New amount (0 to skip): ");
        try { amount = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { amount = 0; }

        System.out.print("New category (press Enter to skip): ");
        String cat = sc.nextLine().trim();

        manager.updateExpense(id, desc, amount, cat);
    }

    static void deleteExpense() {
        System.out.print("🗑️  Enter Expense ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("⚠️  Are you sure? (yes/no): ");
            if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                manager.deleteExpense(id);
            } else {
                System.out.println("Cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID.");
        }
    }

    // ──────────────────────────────────────────
    // UI Helpers
    // ──────────────────────────────────────────

    static void printBanner() {
        System.out.println("""
                ╔═══════════════════════════════════════════╗
                ║        💰 EXPENSE TRACKER CLI             ║
                ║        Track. Analyze. Save More.         ║
                ╚═══════════════════════════════════════════╝
                """);
    }

    static void printMenu() {
        System.out.println("""
                ┌─────────────────────────────┐
                │         MAIN MENU           │
                ├─────────────────────────────┤
                │  1. Add Expense             │
                │  2. View All Expenses       │
                │  3. Filter by Category      │
                │  4. Filter by Month         │
                │  5. Update Expense          │
                │  6. Delete Expense          │
                │  7. Summary & Analytics     │
                │  8. Monthly Report          │
                │  9. Exit                    │
                └─────────────────────────────┘
                Enter choice (1-9): \
                """);
    }
}
