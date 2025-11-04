package hotel.model.filter;

import hotel.users.client.Client;

import java.util.Comparator;

public enum FilterClient {
    SURNAME(Comparator.comparing(Client::getSurname)),
    ID(Comparator.comparing(Client::getId)),
    NUMBERROOM(Comparator.comparing(Client::getNumberRoom)),
    DATECHECKUP(Comparator.comparing(Client::getCheckOutDate));


    private final Comparator<Client> comparator;

    FilterClient(Comparator<Client> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Client> getComparator() {
        return comparator;
    }
}
