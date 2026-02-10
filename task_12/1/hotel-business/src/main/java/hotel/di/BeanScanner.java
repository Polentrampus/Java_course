package hotel.di;

import hotel.annotation.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BeanScanner {
    public List<Class<?>> scan(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace(".", "/");

        URL resource = BeanScanner.class.getClassLoader().getResource(packagePath);
        if (resource != null) {
            File directory = new File(resource.getFile());
            scanDirectory(directory, packagePath, classes);
        }
        return classes;
    }

    private void scanDirectory(File directory, String packagePath, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packagePath + '.' + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packagePath + '.'
                        + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
