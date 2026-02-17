package hotel.di;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.config.HotelConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DIContainer {
    private Map<String, Object> beans = new HashMap<>();
    private Map<Class<?>, String> beanNames = new HashMap<>();
    public static final DIContainer INSTANCE = new DIContainer();

    private DIContainer() {
    }

    public static DIContainer getInstance() {
        return INSTANCE;
    }

    /// Автоматическая инициализация контейнера
    public void init(List<Class<?>> classes, HotelConfiguration config) {
        for (Class<?> clazz : classes) {
            String beanName = generateBeanName(clazz);
            if (!beans.containsKey(beanName)) {
                beanNames.put(clazz, beanName);
                beans.put(beanName, null);
            }
        }

        String roomServiceBeanName = getRoomServiceBeanName(config);
        beans.put("roomService", null);
        beanNames.put(hotel.service.IRoomService.class, "roomService");

        // создаем бины
        for (Class<?> clazz : classes) {
            String beanName = beanNames.get(clazz);
            if (beans.get(beanName) == null) {
                createBean(clazz);
            }
        }

        createObjectServiceImplementation(config);

        // Внедряем зависимости
        for (Object bean : beans.values()) {
            if (bean != null) {
                injectDependencies(bean);
            }
        }
    }

    /// Определяем имя бина для IRoomService в зависимости от конфигурации
    private String getRoomServiceBeanName(HotelConfiguration config) {
        if (config != null && config.isRoomStatusModifiable()) {
            return "modifiableRoomService";
        } else {
            return "readRoomService";
        }
    }

    /// Определяем имя бина для IBookingService в зависимости от конфигурации
    private String getBookingServiceBeanName(HotelConfiguration config) {
        if (config != null && config.isBookingDeletionAllowed()) {
            return "advancedBookingService";
        } else {
            return "bookingService";
        }
    }

    /// Создаем конкретную реализацию для сервиса
    private void createObjectServiceImplementation(HotelConfiguration config) {
        String roomServiceBeanName = getRoomServiceBeanName(config);
        Object roomServiceImpl = beans.get(roomServiceBeanName);

        String bookingServiceBeanName = getBookingServiceBeanName(config);
        Object bookingServiceImpl = beans.get(bookingServiceBeanName);

        if (roomServiceImpl == null || bookingServiceImpl == null) {
            for (Class<?> clazz : beanNames.keySet()) {
                String beanName = beanNames.get(clazz);
                if (beanName.equals(roomServiceBeanName)) {
                    createBean(clazz);
                    roomServiceImpl = beans.get(roomServiceBeanName);
                    break;
                } else if (beanName.equals(bookingServiceBeanName)) {
                    createBean(clazz);
                    bookingServiceImpl = beans.get(bookingServiceBeanName);
                    break;
                }
            }
        }

        if (roomServiceImpl != null) {
            beans.put("roomService", roomServiceImpl);
        }
        if (bookingServiceImpl != null) {
            beans.put("bookingService", bookingServiceImpl);
        }
    }

    /// Внедряет зависимость в бин
    private void injectDependencies(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                injectField(bean, field);
            }
        }
    }

    /// Внедрить в поле
    private void injectField(Object bean, Field field) {
        try {
            Class<?> fieldType = field.getType();
            Object dependency = getBean(fieldType);

            if (dependency == null) {
                throw new RuntimeException("зависимости нет");
            }
            field.setAccessible(true);
            field.set(bean, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Не получилось внедрить зависимость в поле: " + field.getName());
        }
    }

    /// Получить бин
    public <T> T getBean(Class<T> type) {
        for (Object bean : beans.values()) {
            if (type.isInstance(bean)) {
                return type.cast(bean);
            }
        }
        return null;
    }

    ///  Получить бин по имени
    public Object getBean(String name) {
        return beans.get(name);
    }

    ///  Создает бин и регистрирует его
    public void createBean(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = resolveDependency(paramTypes[i]);
            }

            Object instance = constructor.newInstance(params);
            String beanName = generateBeanName(clazz);
            beans.put(beanName, instance);
            beanNames.put(clazz, beanName);
            System.out.println("Создан бин: " + beanName);
        } catch (Exception e) {
            throw new RuntimeException("Не получилось создать бин: " + clazz.getName());
        }
    }

    private Object resolveDependency(Class<?> type) {
        for (Object bean : beans.values()) {
            if (type.isInstance(bean)) {
                return bean;
            }
        }

        if (type.equals(hotel.service.IRoomService.class)) {
            Object roomService = beans.get("roomService");
            if (roomService != null) {
                return roomService;
            }
        }

        if (type == String.class) {
            return "";
        }
        if (type == int.class || type == Integer.class) {
            return 0;
        }
        if (type == boolean.class || type == Boolean.class) {
            return false;
        }

        if (type.isAnnotationPresent(Component.class)) {
            createBean(type);
            return getBean(type);
        }
        throw new RuntimeException("Не могу разрешить зависимость типа: " + type.getName());
    }

    private String generateBeanName(Class<?> clazz) {
        Component annotation = clazz.getAnnotation(Component.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }

        String className = clazz.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}

