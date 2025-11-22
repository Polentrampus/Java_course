package hotel.view;

import hotel.view.action.IAction;

public class MenuItem {
    private String title;
    private IAction action;
    private Menu nextMenu;

    public MenuItem(String title, IAction action) {
        this.title = title;
        this.action = action;
    }

    public static MenuItem createNavigatorMenuItem(String title, Navigator navigator, Menu nextMenu) {
        return new MenuItem(title, () -> navigator.setCurrentMenu(nextMenu));
    }

    public static MenuItem createMenuItem(String title, IAction action) {
        return new MenuItem(title, action);
    }

    public static MenuItem createConditionalItem(String title, IAction action, boolean isAvailable) {
        return new MenuItem(title, isAvailable ? action : () ->
                System.out.println("Этот пункт меню недоступен"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public Menu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(Menu nextMenu) {
        this.nextMenu = nextMenu;
    }
}
