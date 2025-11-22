package hotel.users.employee;

import java.time.LocalDate;

public class Person {
    private final String name;
    private final String surname;
    private final String patronymic;
    private final LocalDate date_of_birth;
    private int id;

    public String getName() {
        return name;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Person(int id, String name, String surname, String patronymic, LocalDate date_of_birth) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.date_of_birth = date_of_birth;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", date_of_birth=" + date_of_birth +
                ", id=" + id +
                '}';
    }
}

