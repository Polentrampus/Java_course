package hotel.model.filter;

import hotel.model.users.client.Client;

import java.util.Comparator;

public enum ClientFilter {
    SURNAME(Comparator.comparing(Client::getSurname)),
    ID(Comparator.comparing(Client::getId)),
    NUMBERROOM(Comparator.comparing(Client::getNumberRoom)),
    DATECHECKUP(Comparator.comparing(Client::getCheckOutDate));


    private final Comparator<Client> comparator;

    ClientFilter(Comparator<Client> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Client> getComparator() {
        return comparator;
    }
}
