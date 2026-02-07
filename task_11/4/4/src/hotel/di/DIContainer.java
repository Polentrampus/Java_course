package hotel.di;

import hotel.annotation.Inject;
import hotel.annotation.Component;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DIContainer {
    private Map<String, Object> beans = new HashMap<>();
    private Map<Class<?>, String> beanNames = new HashMap<>();
    public static final DIContainer INSTANCE = new DIContainer();

    private DIContainer() {}

    public static DIContainer getInstance() {
        return INSTANCE;
    }

    /// Автоматическая инициализация контейнера
    public void init(String basePackage) {
        BeanScanner scanner = new BeanScanner();
        List<Class<?>> classes = scanner.scan(basePackage);

        for (Class<?> clazz : classes) {
            String beanName = generateBeanName(clazz);
            if (!beans.containsKey(beanName)) {
                beanNames.put(clazz, beanName);
                beans.put(beanName, null); // Зарезервируем место
            }
        }

        // создаем бины
        for (Class<?> clazz : classes) {
            String beanName = beanNames.get(clazz);
            if (beans.get(beanName) == null) { // Если еще не создан
                createBean(clazz);
            }
        }

        // Внедряем зависимости
        for (Object bean : beans.values()) {
            if (bean != null) {
                injectDependencies(bean);
            }
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

            if(dependency == null){
                throw new RuntimeException("зависимости нет");
            }
            field.setAccessible(true);
            field.set(bean, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Не получилось внедрить зависимость в поле: " + field.getName());
        }
    }

    /// Получить бин
    public  <T> T getBean(Class<T> type) {
        for(Object bean : beans.values()){
            if(type.isInstance(bean)){
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
    private void createBean(Class<?> clazz) {
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
        }catch (Exception e){
            throw new RuntimeException("Не получилось создать бин: "+clazz.getName());
        }
    }

    private Object resolveDependency(Class<?> type) {
        for (Object bean : beans.values()) {
            if (type.isInstance(bean)) {
                return bean;
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
        if(annotation != null && !annotation.value().isEmpty()){
            return annotation.value();
        }

        String className = clazz.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}

