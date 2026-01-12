package hotel.view.action;

import hotel.controller.AdminController;

import java.util.Scanner;

public class EvictClientOfHotel extends BaseAction{
    AdminController adminController;
    public EvictClientOfHotel(AdminController admin, Scanner scanner) {
        super(scanner);
        this.adminController = admin;
    }

    @Override
    public void execute() {

    }
}
