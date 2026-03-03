package hotel.view.action;

import hotel.exception.HotelException;

import java.time.LocalDate;
import java.util.Scanner;

public abstract class BaseAction implements IAction {
    protected final Scanner scanner;

    public BaseAction(Scanner scanner) {
        this.scanner = scanner;
    }


    protected int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.println(prompt);
            if (!scanner.hasNextInt()) {
                System.out.println("Введите число!");
                scanner.nextLine();
                continue;
            }
            int value = scanner.nextInt();
            if (value < min || value > max) {
                System.out.println("Введите значение в диапазоне от: " + min + " до " + max);
                continue;
            }
            scanner.nextLine();
            return value;
        }
    }

    protected int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            if (!scanner.hasNextInt()) {
                System.out.println("Введите число!");
                scanner.nextLine();
                continue;
            }
            int value = scanner.nextInt();
            scanner.nextLine();
            return value;
        }
    }

    protected String readString(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine().trim();
    }

    protected LocalDate readDate(String prompt) {
        while (true) {
            String input = readString(prompt + " (гггг-мм-дд): ");
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Неверный формат даты! Используйте гггг-мм-дд");
            }
        }
    }

    protected <T extends Enum<T>> T readEnum(Class<T> enumClass, String prompt) {
        while (true) {
            System.out.println(prompt + " " + getEnumOptions(enumClass) + ": ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверное значение! Доступные: " + getEnumOptions(enumClass));
            }
        }
    }

    private <T extends Enum<T>> String getEnumOptions(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        sb.append(")");
        return sb.toString();
    }
}