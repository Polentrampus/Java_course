package hotel.view;

import java.util.Scanner;

public class MenuController {
    private final Navigator navigator;
    boolean running = false;

    public MenuController(Navigator navigator) {
        this.navigator = navigator;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (navigator.isRunning()) {
            navigator.printMenu();
            try {
                int choice = sc.nextInt();
                sc.nextLine();
                navigator.navigate(choice);
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Пожалуйста, введите число.");
                sc.nextLine();
            }
        }
        sc.close();
    }
}
