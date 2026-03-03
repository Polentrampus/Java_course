package hotel.model.users;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.model.Entity;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Entity {
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate date_of_birth;

    public Person(int id, String name, String surname,
                  String patronymic, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.date_of_birth = dateOfBirth;
    }

    public Person() {
    }

    public Integer getId() {
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getDateOfBirth() {
        return date_of_birth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.date_of_birth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", date_of_birth=" + date_of_birth +
                '}';
    }
}

