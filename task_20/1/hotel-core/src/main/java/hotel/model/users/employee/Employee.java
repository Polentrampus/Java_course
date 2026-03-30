package hotel.model.users.employee;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import hotel.model.users.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "id")
public class Employee extends Person {
    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private EmployeeRole position;
    @Column(name = "hire_date")
    private LocalDate hireDate = LocalDate.now();
    private BigDecimal salary;

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
            @JsonProperty("salary") BigDecimal  salary
    ) {
        super(id, "employee", name, surname, patronymic, dateOfBirth);
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public Employee() {
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
                                          String positionStr, BigDecimal salary) {
        EmployeeRole position = EmployeeRole.valueOf(positionStr.toUpperCase());
        return new Employee(id, name, surname, patronymic, dateOfBirth,
                position, LocalDate.now(), salary);
    }
}