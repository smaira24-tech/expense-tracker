package com.expensetracker;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ExpenseManager manager = new ExpenseManager();

    // ANSI Color Codes
    static final String RESET   = "\u001B[0m";
    static final String BOLD    = "\u001B[1m";
    static final String RED     = "\u001B[31m";
    static final String GREEN   = "\u001B[32m";
    static final String YELLOW  = "\u001B[33m";
    static final String BLUE    = "\u001B[34m";
    static final String CYAN    = "\u001B[36m";
    static final String WHITE   = "\u001B[37m";
    static final String BG_GREEN= "\u001B[42m";

    public static void main(String[] args) {
        clearScreen();
        printBanner();
        pause(600);

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print(CYAN + BOLD + "  ➤  Enter choice (1-9): " + RESET);
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
                case "9" -> { printGoodbye(); running = false; }
                default  -> printError("Invalid choice! Please enter a number from 1 to 9.");
            }
            if (running) {
                System.out.print(YELLOW + "\n  Press ENTER to continue..." + RESET);
                sc.nextLine();
                clearScreen();
            }
        }
        sc.close();
    }

    static void addExpense() {
        printSectionHeader("ADD NEW EXPENSE");
        System.out.print(WHITE + "  Description : " + RESET);
        String desc = sc.nextLine().trim();
        if (desc.isEmpty()) { printError("Description cannot be empty!"); return; }

        double amount = 0;
        while (amount <= 0) {
            System.out.print(WHITE + "  Amount (Rs.) : " + RESET);
            try { amount = Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { printError("Enter a valid number!"); }
        }

        System.out.println(YELLOW + "\n  Categories:" + RESET);
        String[] cats = {"Food", "Transport", "Shopping", "Bills", "Health", "Entertainment", "Other"};
        for (int i = 0; i < cats.length; i++) {
            System.out.printf(CYAN + "     [%d] %s%n" + RESET, i + 1, cats[i]);
        }
        System.out.print(WHITE + "\n  Category : " + RESET);
        String category = sc.nextLine().trim();
        if (category.isEmpty()) category = "Other";
        manager.addExpense(desc, amount, category);
        printSuccess("Expense added successfully!");
    }

    static void filterByCategory() {
        printSectionHeader("FILTER BY CATEGORY");
        List<String> catList = manager.getCategories();
        if (catList.isEmpty()) { printError("No categories found yet!"); return; }
        System.out.println(YELLOW + "  Available: " + CYAN + String.join(", ", catList) + RESET);
        System.out.print(WHITE + "\n  Enter category: " + RESET);
        manager.listByCategory(sc.nextLine().trim());
    }

    static void filterByMonth() {
        printSectionHeader("FILTER BY MONTH");
        int month = 0, year = 0;
        while (month < 1 || month > 12) {
            System.out.print(WHITE + "  Month (1-12) : " + RESET);
            try { month = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { printError("Enter a valid month!"); }
        }
        while (year < 2000) {
            System.out.print(WHITE + "  Year (e.g. 2024) : " + RESET);
            try { year = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { printError("Enter a valid year!"); }
        }
        manager.listByMonth(month, year);
    }

    static void updateExpense() {
        printSectionHeader("UPDATE EXPENSE");
        System.out.print(WHITE + "  Enter Expense ID : " + RESET);
        int id;
        try { id = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { printError("Invalid ID!"); return; }
        System.out.print(WHITE + "  New description (Enter to skip) : " + RESET);
        String desc = sc.nextLine().trim();
        double amount = 0;
        System.out.print(WHITE + "  New amount (0 to skip) : " + RESET);
        try { amount = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { amount = 0; }
        System.out.print(WHITE + "  New category (Enter to skip) : " + RESET);
        String cat = sc.nextLine().trim();
        manager.updateExpense(id, desc, amount, cat);
        printSuccess("Expense updated!");
    }

    static void deleteExpense() {
        printSectionHeader("DELETE EXPENSE");
        System.out.print(WHITE + "  Enter Expense ID : " + RESET);
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print(RED + "  Are you sure? (yes/no) : " + RESET);
            if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                manager.deleteExpense(id);
                printSuccess("Expense deleted!");
            } else {
                System.out.println(YELLOW + "  Cancelled." + RESET);
            }
        } catch (NumberFormatException e) { printError("Invalid ID!"); }
    }

    static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║                                                  ║");
        System.out.println("  ║        💰  E X P E N S E   T R A C K E R        ║");
        System.out.println("  ║            Track . Analyze . Save More           ║");
        System.out.println("  ║                                                  ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
        System.out.println(YELLOW + "        Welcome back! Let's manage your money." + RESET);
        System.out.println();
    }

    static void printMenu() {
        System.out.println();
        System.out.println(BOLD + BLUE + "  ┌─────────────────────────────────────────┐" + RESET);
        System.out.println(BOLD + BLUE + "  │" + RESET + BOLD + "              MAIN MENU                   " + BLUE + "│" + RESET);
        System.out.println(BOLD + BLUE + "  ├─────────────────────────────────────────┤" + RESET);
        System.out.println(BLUE + "  │" + GREEN  + "   1. " + WHITE + "Add New Expense                    " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + GREEN  + "   2. " + WHITE + "View All Expenses                  " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + GREEN  + "   3. " + WHITE + "Filter by Category                 " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + GREEN  + "   4. " + WHITE + "Filter by Month                    " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + YELLOW + "   5. " + WHITE + "Update Expense                     " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + YELLOW + "   6. " + WHITE + "Delete Expense                     " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + CYAN   + "   7. " + WHITE + "Summary & Analytics                " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + CYAN   + "   8. " + WHITE + "Monthly Report                     " + BLUE + "  │" + RESET);
        System.out.println(BLUE + "  │" + RED    + "   9. " + WHITE + "Exit                               " + BLUE + "  │" + RESET);
        System.out.println(BOLD + BLUE + "  └─────────────────────────────────────────┘" + RESET);
    }

    static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(BOLD + CYAN + "  ╔══════════════════════════════════════════╗" + RESET);
        System.out.printf( BOLD + CYAN + "  ║  %-40s║%n" + RESET, title);
        System.out.println(BOLD + CYAN + "  ╚══════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    static void printSuccess(String msg) {
        System.out.println();
        System.out.println(BG_GREEN + BOLD + "  ✅  " + msg + "  " + RESET);
    }

    static void printError(String msg) {
        System.out.println();
        System.out.println(RED + BOLD + "  ❌  " + msg + RESET);
    }

    static void printGoodbye() {
        System.out.println();
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║   Thanks for using Expense Tracker!      ║");
        System.out.println("  ║   Keep saving, keep growing!             ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
