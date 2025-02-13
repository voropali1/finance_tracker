package cz.cvut.fel.pm2.TransactionMicroservice.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import cz.cvut.fel.pm2.TransactionMicroservice.dto.ExpenseDTO;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.Expense;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseCategoryRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.service.ExpenseService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * REST controller for managing expenses.
 */
@RestController
@RequestMapping("/transactions/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Adds a new expense.
     *
     * @param expenseDto the expense data transfer object
     * @param userId the ID of the user
     * @return a response indicating the status of the request
     */
    @PostMapping("/add-expense")
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDTO expenseDto, @RequestParam int userId) {
        ExpenseCategory expenseCategory = expenseDto.getExpenseCategory();
        Long expenseCategoryId = expenseDto.getExpenseCategory().getId();

        Optional<ExpenseCategory> optionalExpenseCategory = expenseCategoryRepository.findById(Math.toIntExact(expenseCategoryId));


        if (optionalExpenseCategory.isPresent()) {
            ExpenseCategory retrievedCategory = optionalExpenseCategory.get();

            if (!retrievedCategory.getCategoryName().equals(expenseCategory.getCategoryName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid category name for the given id");
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid expenseCategory id");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        if (expenseDto.getTransactionDate().isAfter(currentDate) || expenseDto.getTransactionDate().isBefore(startDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction date must be between 2000 and the current date");
        }

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setAmount(expenseDto.getAmount());
        expense.setName(expenseDto.getName());
        expense.setTransactionDate(expenseDto.getTransactionDate());
        expense.setExpenseCategory(expenseCategory);

        expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Updates an existing expense.
     *
     * @param id the ID of the expense
     * @param updatedExpense the updated expense data
     * @param userId the ID of the user
     * @return a response indicating the status of the request
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable("id") int id, @RequestBody Expense updatedExpense,  @RequestParam int userId) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense with id " + id + " not found");
        }
        updatedExpense.setId(id);
        expenseService.updateExpense(updatedExpense, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes an existing expense.
     *
     * @param id the ID of the expense
     * @param userId the ID of the user
     * @return a response indicating the status of the request
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("id") int id,  @RequestParam int userId) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense with id " + id + " not found");
        }
        expenseService.deleteExpense(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an expense by its ID and user ID.
     *
     * @param id the ID of the expense
     * @param userId the ID of the user
     * @return the expense with the given ID and user ID, or an error response if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable("id") int id, @RequestParam int userId) {
        try {
            Expense expense = expenseService.getExpenseById(id, userId);
            if (expense == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense with id " + id + " not found");
            }
            return ResponseEntity.ok().body(expense);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }

    /**
     * Retrieves all expenses in descending order by transaction date for a user.
     *
     * @param userId the ID of the user
     * @return a list of expenses in descending order by transaction date
     */
    @GetMapping("/all_expenses_desc")
    public ResponseEntity<List<Expense>> getAllExpensesDesc(@RequestParam int userId) {
        List<Expense> expenses = expenseService.getAllExpensesDescendingOrder(userId);
        return ResponseEntity.ok().body(expenses);
    }

    /**
     * Retrieves all expenses in ascending order by transaction date for a user.
     *
     * @param userId the ID of the user
     * @return a list of expenses in ascending order by transaction date
     */
    @GetMapping("/all_expenses_asc")
    public ResponseEntity<List<Expense>> getAllExpensesAsc(@RequestParam int userId) {
        List<Expense> expenses = expenseService.getAllExpensesAscendingOrder(userId);
        return ResponseEntity.ok().body(expenses);
    }

    /**
     * Adds a new expense category.
     *
     * @param requestBody a map containing the category name
     * @return a response indicating the status of the request
     */
    @PostMapping("/add-category")
    public ResponseEntity<?> addExpenseCategory(@RequestBody Map<String, String> requestBody) {
        String categoryName = requestBody.get("categoryName");
        expenseService.addExpenseCategory(categoryName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves all expense categories.
     *
     * @return a list of all expense categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategory>> getAllExpenseCategories() {
        List<ExpenseCategory> categories = expenseService.getAllExpenseCategories();
        return ResponseEntity.ok().body(categories);
    }

    /**
     * Retrieves expenses by category ID and user ID.
     *
     * @param categoryId the ID of the expense category
     * @param userId the ID of the user
     * @return a list of expenses in the specified category for the specified user
     */
    @GetMapping("/expenses-by-category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam int userId) {

        Optional<ExpenseCategory> expenseCategory = expenseCategoryRepository.findById(Math.toIntExact(categoryId));

        if (!expenseCategory.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Expense> expenses = expenseService.getExpensesByExpenseCategory(expenseCategory.get(), userId);
        return ResponseEntity.ok(expenses);
    }


    /**
     * Filters expenses by amount range for a user.
     *
     * @param fromAmount the minimum amount
     * @param toAmount the maximum amount (optional)
     * @param userId the ID of the user
     * @return a list of expenses within the specified amount range
     */
    @GetMapping("/filter-by-amount")
    public ResponseEntity<List<Expense>> filterExpensesByAmountRange(
            @RequestParam("from") float fromAmount,
            @RequestParam(value = "to", required = false) Float toAmount,
            @RequestParam int userId) {

        if (toAmount != null && fromAmount > toAmount) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Expense> filteredExpenses;
        if (toAmount != null) {
            filteredExpenses = expenseService.filterExpensesByAmountRange(userId, fromAmount, toAmount);
        } else {
            filteredExpenses = expenseService.filterExpensesByAmountStartingFrom(userId, fromAmount);
        }

        return ResponseEntity.ok().body(filteredExpenses);
    }

    /**
     * Exports all expenses to an Excel file.
     */
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response, @RequestParam int userId) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses.xlsx";
        response.setHeader(headerKey, headerValue);

        List<Expense> expenses = expenseService.getAllExpensesDescendingOrder(userId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");
        writeHeaderLine(sheet);
        writeDataLines(expenses, sheet);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    /**
     * Exports all expenses to a PDF file.
     */
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response, @RequestParam int userId) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses.pdf";
        response.setHeader(headerKey, headerValue);

        List<Expense> expenses = expenseService.getAllExpensesDescendingOrder(userId);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        addPDFContent(document, expenses);
        document.close();
    }

    // Helper methods for Excel and PDF generation
    private void writeHeaderLine(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);

        String[] headers = {"ID", "Name", "Amount", "Transaction Date", "Category"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeDataLines(List<Expense> expenses, Sheet sheet) {
        int rowCount = 1;
        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            row.createCell(columnCount++).setCellValue(expense.getId());
            row.createCell(columnCount++).setCellValue(expense.getName());
            row.createCell(columnCount++).setCellValue(expense.getAmount());
            row.createCell(columnCount++).setCellValue(expense.getTransactionDate().toString());
            row.createCell(columnCount++).setCellValue(expense.getExpenseCategory().getCategoryName());
        }
    }

    private void addPDFContent(Document document, List<Expense> expenses) throws DocumentException {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        Paragraph title = new Paragraph("Expenses Report", font);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        writePDFTableHeader(table);
        writePDFTableData(table, expenses);

        document.add(table);
    }

    private void writePDFTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);

        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(BaseColor.WHITE);

        String[] headers = {"ID", "Name", "Amount", "Transaction Date", "Category"};
        for (String header : headers) {
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    private void writePDFTableData(PdfPTable table, List<Expense> expenses) {
        for (Expense expense : expenses) {
            table.addCell(String.valueOf(expense.getId()));
            table.addCell(expense.getName());
            table.addCell(String.valueOf(expense.getAmount()));
            table.addCell(expense.getTransactionDate().toString());
            table.addCell(expense.getExpenseCategory().getCategoryName());
        }
    }
}