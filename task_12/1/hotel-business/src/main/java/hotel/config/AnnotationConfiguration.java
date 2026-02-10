package hotel.config;

import hotel.annotation.Component;
import hotel.annotation.ConfigProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component
public class AnnotationConfiguration {
    public static void config(Object configObject) throws Exception {
        Class<?> clazz = configObject.getClass();
        Properties properties = loadProperties(configObject);
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                applyProperty(configObject, field, properties, annotation);
            }
        }
    }

    /// Применяет свойство к полю
    private static void applyProperty(Object configObject, Field field, Properties properties, ConfigProperty annotation) throws IOException, IllegalAccessException {
        field.setAccessible(true);

        String name = getPropertyName(annotation, field);
        String stringValue = properties.getProperty(name);

        if (stringValue == null) {
            return; // если значение нет, то оставляем дефолтное
        }
        Object value = convertValue(stringValue, field, annotation.valueType());
        field.set(configObject, value);
    }

    /// Определяем имя свойства для поиска в файле
    private static String getPropertyName(ConfigProperty annotation, Field field) {
        if (!annotation.propertyName().isEmpty()) {
            return annotation.propertyName();
        }
        // например: PropertiesConfiguration.roomStatusModifiable
        return field.getDeclaringClass().getSimpleName().toLowerCase()
                + "." + field.getName();
    }

    /// Конвертирует тип переменной из string
    private static Object convertValue(String stringValue, Field field, Class<?> valueType) throws IllegalAccessException {
        Class<?> targetType;
        // если в аннотации указан тип, то импользуем его, если нет, то берем тип поля
        if (valueType != String.class) {
            targetType = valueType;
        } else {
            targetType = field.getType();
            if (targetType == Object.class) {
                targetType = String.class;
            }
        }

        return convertStringToType(stringValue.trim(), targetType, field);
    }

    /// Конвертирует в конкретный тип
    private static Object convertStringToType(String value, Class<?> targetType, Field field) {
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == String.class) {
            return value;

            // Для массивов
        } else if (targetType.isArray()) {
            return convertToArray(value, targetType.getComponentType(), field);

            // Для коллекций
        } else if (Collection.class.isAssignableFrom(targetType)) {
            return convertToCollection(value, targetType);

            // Для других случаев можно добавить кастомные конвертеры
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot convert '%s' to type '%s' for field '%s'",
                            value, targetType, field.getName()));
        }
    }

    /// Конвертируем строку в коллекцию
    private static Collection<?> convertToCollection(String value, Class<?> collectionType) {
        Collection<Object> collection;

        if (collectionType == List.class || collectionType == ArrayList.class) {
            collection = new ArrayList<>();
        } else if (collectionType == Set.class || collectionType == HashSet.class) {
            collection = new HashSet<>();
        } else if (collectionType == LinkedList.class) {
            collection = new LinkedList<>();
        } else {
            collection = new ArrayList<>();
        }
        if (value.isEmpty()) {
            return collection;
        }

        String[] parts = value.split(";");
        for (String part : parts) {
            collection.add(part.trim());
        }

        return collection;
    }

    /**
     * Конвертирует строку в массив
     *
     * @param value         строка со значениями, разделенными ';'
     * @param componentType тип элементов массива
     * @param fieldName     имя поля
     */
    private static Object convertToArray(String value, Class<?> componentType, Field fieldName) {
        String field = (fieldName != null) ? fieldName.getName() : "unknown";
        if (value.isEmpty()) {
            return java.lang.reflect.Array.newInstance(componentType, 0);
        }
        String[] split = value.split(";");
        Object array = java.lang.reflect.Array.newInstance(componentType, split.length);
        for (int i = 0; i < split.length; i++) {
            Object element = convertStringToType(split[i].trim(), componentType, fieldName);
            java.lang.reflect.Array.set(array, i, element);
        }
        return array;
    }

    /// Определяем какой properties файл загружать
    private static Properties loadProperties(Object configObject) throws IOException {
        String fileName = "hotel.properties";

        for (Field field : configObject.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                if (!annotation.configFileName().equals(fileName)) {
                    fileName = annotation.configFileName();
                    break;
                }
            }
        }
        return loadPropertiesFromFile(fileName);
    }

    /// Загружает properties из файла в classpath
    private static Properties loadPropertiesFromFile(String fileName) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = AnnotationConfiguration.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new FileNotFoundException(
                        "Конфигурационный класс не найден в classpath   : " + fileName);
            }

            properties.load(input);
        }

        return properties;
    }
}
