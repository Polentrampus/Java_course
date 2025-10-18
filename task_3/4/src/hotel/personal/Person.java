package hotel.personal;

public class Person {
    String name;
    String surname;
    String patronymic;
    int date_of_birth;
    int month_of_birth;
    int year_of_birth;
    private static int lastId = 0;
    private final int id;

    public Person(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.date_of_birth = date_of_birth;
        this.month_of_birth = month_of_birth;
        this.year_of_birth = year_of_birth;
        this.id = lastId++;
    }

    public int getId() {
        return id;
    }
}
