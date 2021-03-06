package com.netcracker.edu.trainee.firstLab.service.reflexion;

import com.netcracker.edu.trainee.firstLab.service.annotations.LabInjector;
import com.netcracker.edu.trainee.firstLab.service.exceptions.InjectorException;
import ru.vsu.lab.repository.IRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reflector {

    public static IRepository inject(IRepository repository) throws InjectorException {
        Field[] fields = repository.getClass().getDeclaredFields();
        List<Field> reflectedFields = new ArrayList<>();
        String separator = File.separator;
        String filePath = "src" + separator + "main" + separator + "resources" + separator + "sort_properties";

        File file = new File(filePath);
        for (Field field : fields)
            if (field.getAnnotation(LabInjector.class) != null)
                reflectedFields.add(field);

        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new InjectorException("File sort_properties not found!");
        }
        scanner.useDelimiter(" = |\n");
        while (scanner.hasNext()) {
            String fieldName = scanner.next();
            String className = scanner.next();
            for (int i = 0; i < reflectedFields.size(); i++) {
                Field field = reflectedFields.get(i);
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        field.set(repository, Class.forName(className).newInstance());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        throw new InjectorException(e);
                    }
                }
            }
        }
        scanner.close();
        return repository;
    }
}
