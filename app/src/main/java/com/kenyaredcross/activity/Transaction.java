package com.kenyaredcross.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Transaction {
    private String id;
    private double amount;
    private LocalDate date;
    private LocalTime time;
    private String paymentMethod;
    private String status;
    private String userEmail;
    private String paymentDetails;
    private String courseId;
    private String fullName;
    private String phoneNumber;
    private String category;
    private String itemName;
    private int requestCount;
    private String supplier;
    private String inventoryManager;
    private String donationId;
    private LocalDateTime donationTime;

    private Transaction(Builder builder) {
        this.id = builder.id;
        this.amount = builder.amount;
        this.date = builder.date;
        this.time = builder.time;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
        this.userEmail = builder.userEmail;
        this.paymentDetails = builder.paymentDetails;
        this.courseId = builder.courseId;
        this.fullName = builder.fullName;
        this.phoneNumber = builder.phoneNumber;
        this.category = builder.category;
        this.itemName = builder.itemName;
        this.requestCount = builder.requestCount;
        this.supplier = builder.supplier;
        this.inventoryManager = builder.inventoryManager;
        this.donationId = builder.donationId;
        this.donationTime = builder.donationTime;
    }

    // Getters
    public String getId() { return id; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getUserEmail() { return userEmail; }
    public String getPaymentDetails() { return paymentDetails; }
    public String getCourseId() { return courseId; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCategory() { return category; }
    public String getItemName() { return itemName; }
    public int getRequestCount() { return requestCount; }
    public String getSupplier() { return supplier; }
    public String getInventoryManager() { return inventoryManager; }
    public String getDonationId() { return donationId; }
    public LocalDateTime getDonationTime() { return donationTime; }

    // String getters for backward compatibility
    public String getAmountAsString() { return String.valueOf(amount); }
    public String getDateAsString() { return date != null ? date.toString() : null; }
    public String getTimeAsString() { return time != null ? time.toString() : null; }
    public String getRequestCountAsString() { return String.valueOf(requestCount); }
    public String getDonationTimeAsString() { return donationTime != null ? donationTime.toString() : null; }

    // Builder class for Transaction
    public static class Builder {
        private String id;
        private double amount;
        private LocalDate date;
        private LocalTime time;
        private String paymentMethod;
        private String status;
        private String userEmail;
        private String paymentDetails;
        private String courseId;
        private String fullName;
        private String phoneNumber;
        private String category;
        private String itemName;
        private int requestCount;
        private String supplier;
        private String inventoryManager;
        private String donationId;
        private LocalDateTime donationTime;

        public Builder(String id) {
            this.id = id;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder paymentDetails(String paymentDetails) {
            this.paymentDetails = paymentDetails;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder requestCount(int requestCount) {
            this.requestCount = requestCount;
            return this;
        }

        public Builder supplier(String supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder inventoryManager(String inventoryManager) {
            this.inventoryManager = inventoryManager;
            return this;
        }

        public Builder donationId(String donationId) {
            this.donationId = donationId;
            return this;
        }

        public Builder donationTime(LocalDateTime donationTime) {
            this.donationTime = donationTime;
            return this;
        }

        // String parameter setters for backward compatibility
        public Builder amountFromString(String amount) {
            this.amount = Double.parseDouble(amount);
            return this;
        }

        public Builder dateFromString(String date) {
            if (date != null && !date.isEmpty()) {
                this.date = LocalDate.parse(date);
            }
            return this;
        }

        public Builder timeFromString(String time) {
            if (time != null && !time.isEmpty()) {
                this.time = LocalTime.parse(time);
            }
            return this;
        }

        public Builder requestCountFromString(String requestCount) {
            if (requestCount != null && !requestCount.isEmpty()) {
                this.requestCount = Integer.parseInt(requestCount);
            }
            return this;
        }

        public Builder donationTimeFromString(String donationTime) {
            if (donationTime != null && !donationTime.isEmpty()) {
                this.donationTime = LocalDateTime.parse(donationTime);
            }
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    // Factory methods to create transactions similar to the original constructors
    public static Transaction createCourseTransaction(String id, String amount, String date, String time,
                                                      String paymentMethod, String status, String userEmail, String paymentDetails, String courseId) {

        return new Builder(id)
                .amountFromString(amount)
                .dateFromString(date)
                .timeFromString(time)
                .paymentMethod(paymentMethod)
                .status(status)
                .userEmail(userEmail)
                .paymentDetails(paymentDetails)
                .courseId(courseId)
                .build();
    }

    public static Transaction createBasicTransaction(String id, String amount, String paymentMethod,
                                                     String email, String fullName, String phoneNumber) {

        return new Builder(id)
                .amountFromString(amount)
                .paymentMethod(paymentMethod)
                .userEmail(email)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static Transaction createDonationTransaction(String id, String amount, String donationTime,
                                                        String email, String fullName, String paymentMethod, String phoneNumber, String status) {

        return new Builder(id)
                .amountFromString(amount)
                .donationTimeFromString(donationTime)
                .userEmail(email)
                .fullName(fullName)
                .paymentMethod(paymentMethod)
                .phoneNumber(phoneNumber)
                .status(status)
                .build();
    }

    public static Transaction createInventoryTransaction(String id, String amount, String category,
                                                         String dateTime, String inventoryManager, String itemName, String requestCount,
                                                         String status, String supplier) {

        return new Builder(id)
                .amountFromString(amount)
                .category(category)
                .dateFromString(dateTime)
                .inventoryManager(inventoryManager)
                .itemName(itemName)
                .requestCountFromString(requestCount)
                .status(status)
                .supplier(supplier)
                .build();
    }
}