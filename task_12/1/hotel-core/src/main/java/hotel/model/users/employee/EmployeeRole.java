package hotel.model.users.employee;

public enum EmployeeRole {
    ADMIN,
    MAID,
    MENDER;

    public String getDisplayName() {
        return switch (this) {
            case ADMIN -> "Администратор";
            case MAID -> "Горничная";
            case MENDER -> "Мастер";
        };
    }
}
