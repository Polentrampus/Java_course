package hotel.model.booking;

class BookingServiceId implements java.io.Serializable {
    private Long bookingId;
    private Long serviceId;

    public BookingServiceId(Long bookingId, Long serviceId) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
    }

    public BookingServiceId() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
