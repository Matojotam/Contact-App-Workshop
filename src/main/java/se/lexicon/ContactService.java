
package se.lexicon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContactService {

    private final List<Contact> contacts = new ArrayList<>();

    // Add a new contact.
    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException(
                    "Contact cannot be null."
            );
        }

        String name = contact.getName() == null
                ? "" : contact.getName().trim();

        String phone = contact.getPhone() == null
                ? "" : contact.getPhone().trim();

        String email = contact.getEmail() == null
                ? "" : contact.getEmail().trim();

        validateName(name);
        validatePhone(phone);

        if (findByPhone(phone) != null) {
            throw new IllegalArgumentException(
                    "That phone number already exists."
            );
        }

        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);

        contacts.add(contact);
    }

    // Return contacts sorted alphabetically by name.
    public List<Contact> getAllContacts() {
        List<Contact> sorted = new ArrayList<>(contacts);

        sorted.sort(
                Comparator.comparing(
                        Contact::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return sorted;
    }

    // Search by name or phone number.
    public List<Contact> searchContacts(String query) {
        List<Contact> matches = new ArrayList<>();

        if (query == null || query.isBlank()) {
            return matches;
        }

        String searchTerm = query.trim().toLowerCase(Locale.ROOT);

        for (Contact contact : contacts) {
            if (contact.getName().toLowerCase(Locale.ROOT)
                    .contains(searchTerm)
                    || contact.getPhone().contains(searchTerm)) {
                matches.add(contact);
            }
        }

        matches.sort(
                Comparator.comparing(
                        Contact::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return matches;
    }

    // Find a contact by phone number.
    public Contact findByPhone(String phone) {
        if (phone == null) {
            return null;
        }

        String searchPhone = phone.trim();

        for (Contact contact : contacts) {
            if (contact.getPhone().equals(searchPhone)) {
                return contact;
            }
        }

        return null;
    }

    // Update an existing contact.
    public boolean updateContact(
            String currentPhone,
            String newName,
            String newPhone,
            String newEmail) {

        Contact contact = findByPhone(currentPhone);

        if (contact == null) {
            return false;
        }

        String name = newName == null ? "" : newName.trim();
        String phone = newPhone == null ? "" : newPhone.trim();
        String email = newEmail == null ? "" : newEmail.trim();

        validateName(name);
        validatePhone(phone);

        Contact existing = findByPhone(phone);

        if (existing != null && existing != contact) {
            throw new IllegalArgumentException(
                    "That phone number already exists."
            );
        }

        // Only change the contact after validation succeeds.
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);

        return true;
    }

    // Delete a contact by phone number.
    public boolean deleteContact(String phone) {
        Contact contact = findByPhone(phone);

        if (contact == null) {
            return false;
        }

        contacts.remove(contact);
        return true;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty."
            );
        }
    }

    private void validatePhone(String phone) {
        if (phone == null
                || !phone.matches("[+]?[-()\\d\\s]+")
                || !phone.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Invalid phone number. Use digits and optional "
                            + "spaces, +, parentheses or hyphens."
            );
        }
    }
}
