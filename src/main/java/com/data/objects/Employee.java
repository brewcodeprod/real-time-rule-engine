package com.data.objects;

import java.util.Objects;

public class Employee implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    //@org.kie.api.definition.type.Label(value = "Id")
    private int id;
    //@org.kie.api.definition.type.Label(value = "Category")
    private String name;
    //@org.kie.api.definition.type.Label(value = "Status")
    private String email;
    //@org.kie.api.definition.type.Label(value = "Price")
    private int creditBalance;
    private String ruleName;

    public Employee() {
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Employee(int id, String name, String email, int creditBalance, String ruleName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.creditBalance = creditBalance;
        this.ruleName = ruleName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(int creditBalance) {
        this.creditBalance = creditBalance;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", creditBalance=" + creditBalance +
                ", ruleName='" + ruleName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return name.equals(employee.name) && email.equals(employee.email);
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
