
package se.lexicon;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContactFileService {

    private final Path filePath = Path.of("contacts.xlsx");

    // Load contacts from Excel.
    public List<Contact> importContacts() throws IOException {
        List<Contact> contacts = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return contacts;
        }

        try (InputStream input = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(input)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // Row 0 contains the column headings.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String name = getCellValue(row, 0, formatter);
                String phone = getCellValue(row, 1, formatter);
                String email = getCellValue(row, 2, formatter);

                // Skip completely empty rows.
                if (name.isBlank() && phone.isBlank()
                        && email.isBlank()) {
                    continue;
                }

                contacts.add(new Contact(name, phone, email));
            }
        }

        return contacts;
    }

    // Save contacts to Excel.
    public void exportContacts(List<Contact> contacts)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Phone");
            header.createCell(2).setCellValue("Email");

            int rowNumber = 1;

            for (Contact contact : contacts) {
                Row row = sheet.createRow(rowNumber++);

                row.createCell(0).setCellValue(contact.getName());
                row.createCell(1).setCellValue(contact.getPhone());
                row.createCell(2).setCellValue(contact.getEmail());
            }

            for (int column = 0; column < 3; column++) {
                sheet.autoSizeColumn(column);
            }

            try (OutputStream output =
                         Files.newOutputStream(filePath)) {
                workbook.write(output);
            }
        }
    }

    private String getCellValue(
            Row row,
            int column,
            DataFormatter formatter) {

        Cell cell = row.getCell(
                column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        );

        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }
}
