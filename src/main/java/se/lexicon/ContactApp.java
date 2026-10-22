package se.lexicon;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ContactApp {

    private final Scanner scanner = new Scanner(System.in);
    private final ContactService contactService = new ContactService();
    private final ContactFileService fileService = new ContactFileService();

    static void main() {
        ContactApp app = new ContactApp();
        app.run();
    }

    public void run() {
        // Load contacts from Excel when the application starts.
        loadContacts();

        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addContact();
                    break;
                case "2":
                    viewContacts();
                    break;
                case "3":
                    searchContacts();
                    break;
                case "4":
                    updateContact();
                    break;
                case "5":
                    deleteContact();
                    break;
                case "6":
                    saveContacts();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    private void displayMenu() {
        System.out.println("\n=== Contact Management System ===");
        System.out.println("1. Add contact");
        System.out.println("2. View all contacts");
        System.out.println("3. Search contacts");
        System.out.println("4. Update contact");
        System.out.println("5. Delete contact");
        System.out.println("6. Save and exit");
        System.out.print("Choose an option: ");
    }

    private void addContact() {
        System.out.println("\n--- Add Contact ---");

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        try {
            Contact contact = new Contact(name, phone, email);
            contactService.addContact(contact);
            System.out.println("Contact added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not add contact: " + e.getMessage());
        }
    }

    private void viewContacts() {
        System.out.println("\n--- All Contacts ---");
        List<Contact> contacts = contactService.getAllContacts();
        printContacts(contacts);
    }

    private void searchContacts() {
        System.out.println("\n--- Search Contacts ---");

        System.out.print("Enter a name or phone number: ");
        String query = scanner.nextLine().trim();

        List<Contact> results = contactService.searchContacts(query);
        printContacts(results);
    }

    private void updateContact() {
        System.out.println("\n--- Update Contact ---");

        System.out.print("Enter the current phone number of the contact: ");
        String currentPhone = scanner.nextLine().trim();

        Contact existing = contactService.findByPhone(currentPhone);

        if (existing == null) {
            System.out.println("Contact not found.");
            return;
        }

        System.out.println("Current contact: " + existing);

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine().trim();

        System.out.print("Enter new phone: ");
        String newPhone = scanner.nextLine().trim();

        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine().trim();

        try {
            boolean updated = contactService.updateContact(
                    currentPhone, newName, newPhone, newEmail
            );

            if (updated) {
                System.out.println("Contact updated successfully.");
            } else {
                System.out.println("Contact could not be updated.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Could not update contact: " + e.getMessage());
        }
    }

    private void deleteContact() {
        System.out.println("\n--- Delete Contact ---");

        System.out.print("Enter the phone number of the contact to delete: ");
        String phone = scanner.nextLine().trim();

        Contact existing = contactService.findByPhone(phone);

        if (existing == null) {
            System.out.println("Contact not found.");
            return;
        }

        System.out.println("Contact to delete: " + existing);
        System.out.print("Are you sure? (y/n): ");
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("y")) {
            boolean deleted = contactService.deleteContact(phone);

            if (deleted) {
                System.out.println("Contact deleted successfully.");
            } else {
                System.out.println("Contact could not be deleted.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void printContacts(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }

        for (Contact contact : contacts) {
            System.out.println(contact);
        }

        System.out.println("Total contacts: " + contacts.size());
    }

    private void loadContacts() {
        try {
            List<Contact> imported = fileService.importContacts();
            int loaded = 0;

            for (Contact contact : imported) {
                try {
                    contactService.addContact(contact);
                    loaded++;
                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Skipped imported contact: " + e.getMessage()
                    );
                }
            }

            System.out.println("Loaded " + loaded + " contacts.");
        } catch (IOException e) {
            System.out.println(
                    "Could not import contacts: " + e.getMessage()
            );
        }
    }

    private void saveContacts() {
        try {
            fileService.exportContacts(contactService.getAllContacts());
            System.out.println("Contacts saved to contacts.xlsx.");
        } catch (IOException e) {
            System.out.println(
                    "Could not save contacts: " + e.getMessage()
            );
        }
    }
}
