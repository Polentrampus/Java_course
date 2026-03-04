package hotel.model.filter;

import hotel.model.users.client.Client;

import java.util.Comparator;

public enum ClientFilter {
    SURNAME(Comparator.comparing(Client::getSurname)),
    ID(Comparator.comparing(Client::getId));

    private final Comparator<Client> comparator;

    ClientFilter(Comparator<Client> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Client> getComparator() {
        return comparator;
    }
}
