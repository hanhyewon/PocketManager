package com.example.soyeon;

public class ExpenseDTO {
    private String expenseId;
    private String date;
    private String context;
    private String charge;
    private String group;

    public ExpenseDTO(String expenseId, String date, String context, String charge, String group) {
        this.expenseId = expenseId;
        this.date = date;
        this.context = context;
        this.charge = charge;
        this.group = group;
    }

    public ExpenseDTO(String date, String context, String charge, String group) {
        this.date = date;
        this.context = context;
        this.charge = charge;
        this.group = group;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
