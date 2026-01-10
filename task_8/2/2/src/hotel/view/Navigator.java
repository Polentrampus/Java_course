package hotel.view;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class Navigator {
    private final Deque<Menu> menuStack = new ArrayDeque<>();
    private boolean running = true;

    public Navigator() {
    }

    public Optional<Menu> getCurrentMenu() {
        return Optional.ofNullable(menuStack.peek());
    }

    public void setCurrentMenu(Menu menu) {
        menuStack.push(menu);
    }

    public void printMenu() {
        getCurrentMenu().ifPresentOrElse(
                currentMenu -> {
                    System.out.println("\n=== " + currentMenu.getName() + " ===");

                    currentMenu.getMenuItems().ifPresent(items -> {
                        for (int i = 0; i < items.size(); i++) {
                            System.out.println((i + 1) + ") " + items.get(i).getTitle());
                        }
                    });

                    System.out.println(menuStack.size() > 1 ? "0) <--- Назад---" : "0) <--> Выход <-->");
                    System.out.print("Выберите пункт: ");
                },
                () -> System.out.println("Нет активного меню!")
        );
    }

    public boolean navigate(int choice) {
        Optional<Menu> currentMenuOpt = getCurrentMenu();
        if (currentMenuOpt.isEmpty()) {
            System.out.println("Нет активного меню для навигации!");
            return false;
        }
        Menu currentMenu = currentMenuOpt.get();
        if (choice == 0) {
            return handleBackOrExit();
        }
        return handleMenuSelection(currentMenu, choice);
    }

    private boolean handleBackOrExit() {
        if (menuStack.size() > 1) {
            menuStack.pop();
            return true;
        } else {
            running = false;
            System.out.println("До свидания!");
            return false;
        }
    }

    private boolean handleMenuSelection(Menu currentMenu, int choice) {
        return currentMenu.getMenuItems()
                .filter(items -> choice > 0 && choice <= items.size())
                .map(items -> {
                    MenuItem selectedItem = items.get(choice - 1);
                    executeMenuItemAction(selectedItem);
                    navigateToNextMenu(selectedItem);
                    return true;
                })
                .orElseGet(() -> {
                    System.out.println("Неверный выбор! Введите число от 0 до " +
                            currentMenu.getMenuItems().map(items -> items.size()).orElse(0));
                    return false;
                });
    }

    private void executeMenuItemAction(MenuItem menuItem) {
        try {
            if (menuItem.getAction() != null) {
                menuItem.getAction().execute();
            }
        } catch (Exception e) {
            System.out.println("Ошибка выполнения действия: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToNextMenu(MenuItem menuItem) {
        if (menuItem.getNextMenu() != null) {
            menuStack.push(menuItem.getNextMenu());
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
    }

    public int getMenuStackSize() {
        return menuStack.size();
    }

    public void forceClearStack() {
        menuStack.clear();
    }
}