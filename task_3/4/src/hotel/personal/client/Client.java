package hotel.personal.client;

import hotel.model.service.Services;
import hotel.personal.Person;

import java.util.Collection;
import java.util.List;

public class Client extends Person {
    private static int lastId = 0;
    private final int id;
    private List<Services> servicesList;
    private int idRoom;

    public Client(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
        System.out.println("Вы пригласили клиента");
        this.id = ++lastId;
    }
    public void addService(String nameService){
        if(Services.findService(nameService) != null)
            servicesList.add(Services.findService(nameService));
    }

    public int getId() {
        return id;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public void request(Collection<Services> services){
        int count = 1;
        System.out.println("Клиент хочет добавить следующие услуги:");
        for(Services service : services){
            System.out.printf("%d) %s", count, service.getDescription());
            count++;
            servicesList.add(service);
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                '}';
    }
}
