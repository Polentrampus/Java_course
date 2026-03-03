package hotel.view;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Menu {
    private String name;
    private List<MenuItem> menuItems = new ArrayList<>();

    public Menu(String name, List<MenuItem> menuItems) {
        this.name = name;
        this.menuItems = menuItems;
    }

    public Menu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<List<MenuItem>> getMenuItems() {
        return Optional.ofNullable(menuItems);
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            System.out.println("параметр имеет значение null!");
            return;
        }
        menuItems.add(menuItem);
    }

    public Optional<MenuItem> getMenuItem(int index) {
        if (index < 0 || index >= menuItems.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(menuItems.get(index));
    }
}
