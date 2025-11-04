package hotel.model.filter;

import hotel.model.service.Services;

import java.util.Comparator;

public enum FilterServices {
    PRICE(Comparator.comparing(Services::getPrice));

    private final Comparator<Services> comparator;

    FilterServices(Comparator<Services> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Services> getComparator() {
        return comparator;
    }
}
