package hotel.model.users.employee;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import hotel.service.export_import.Entity;
import hotel.model.users.Person;

import java.time.LocalDate;

public class Employee extends Person implements Entity {
    private EmployeeRole position;
    private LocalDate hireDate;
    private double salary;

    @JsonIgnore
    private transient boolean isCleaning = false;
    @JsonIgnore
    private transient boolean isFixing = false;

    @JsonCreator
    public Employee(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("surname") String surname,
            @JsonProperty("patronymic") String patronymic,
            @JsonProperty("date_of_birth") LocalDate dateOfBirth,
            @JsonProperty("position") EmployeeRole position,
            @JsonProperty("hire_date") LocalDate hireDate,
            @JsonProperty("salary") double salary
    ) {
        super(id, name, surname, patronymic, dateOfBirth);
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public Employee(int id, String name, String surname, String patronymic,
                    LocalDate dateOfBirth, EmployeeRole position, LocalDate hireDate) {
        this(id, name, surname, patronymic, dateOfBirth, position, hireDate, 0.0);
    }

    public EmployeeRole getPosition() {
        return position;
    }

    public void setPosition(EmployeeRole position) {
        this.position = position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void handleRequest(int roomId, String requestType) {
        switch (position) {
            case ADMIN:
                handleAdminRequest(roomId, requestType);
                break;
            case MAID:
                handleMaidRequest(roomId, requestType);
                break;
            case MENDER:
                handleMenderRequest(roomId, requestType);
                break;
        }
    }

    private void handleAdminRequest(int roomId, String requestType) {
        System.out.printf("Администратор %s получил запрос '%s' для комнаты %d\n",
                getFullName(), requestType, roomId);
    }

    private void handleMaidRequest(int roomId, String requestType) {
        if ("CLEANING".equals(requestType)) {
            isCleaning = true;
            System.out.printf("Горничная %s начала уборку номера %d\n",
                    getFullName(), roomId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.printf("Горничная %s закончила уборку номера %d\n",
                    getFullName(), roomId);
            isCleaning = false;
        }
    }

    private void handleMenderRequest(int roomId, String requestType) {
        if ("REPAIR".equals(requestType) || "MAINTENANCE".equals(requestType)) {
            isFixing = true;
            System.out.printf("Мастер %s начал ремонт номера %d\n",
                    getFullName(), roomId);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.printf("Мастер %s закончил ремонт номера %d\n",
                    getFullName(), roomId);
            isFixing = false;
        }
    }

    public boolean isCleaning() {
        return isCleaning;
    }

    public boolean isFixing() {
        return isFixing;
    }

    public String getFullName() {
        return String.format("%s %s %s", getSurname(), getName(), getPatronymic());
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', position=%s, hireDate=%s, salary=%s}",
                getId(), getFullName(), position, hireDate, salary);
    }

    public static Employee createEmployee(int id, String name, String surname,
                                          String patronymic, LocalDate dateOfBirth,
                                          String positionStr) {
        EmployeeRole position = EmployeeRole.valueOf(positionStr.toUpperCase());
        return new Employee(id, name, surname, patronymic, dateOfBirth,
                position, LocalDate.now());
    }
}