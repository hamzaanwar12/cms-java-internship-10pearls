package com.recky.demo.util;

public class ApiResponse<T> {
    private int statusCode; // HTTP status code
    private String status; // "success" or "error"
    private String message;
    private T data;
    private Integer totalPages;  // Added total pages
    private Integer currentPage; // Added current page
    private Integer totalRecords; // Added total records (optional)

    // Constructor for non-paginated response
    public ApiResponse(int statusCode, String status, String message, T data) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.data = data;
        this.totalPages = null;
        this.currentPage = null;
        this.totalRecords = null;
    }

    // Constructor for paginated response
    public ApiResponse(int statusCode, String status, String message, T data, Integer totalPages, Integer currentPage, Integer totalRecords) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.data = data;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.totalRecords = totalRecords;
    }

    // Getters and Setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
}
