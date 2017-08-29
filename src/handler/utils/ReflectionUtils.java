package handler.utils;

import handler.observable.ObservableValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReflectionUtils {

    public static <T extends Annotation> Optional<T> GetAnnotation(AnnotatedElement cls, Class<T> annotationCls) {
        if(!cls.isAnnotationPresent(annotationCls))
            return Optional.empty();
        return Optional.of(cls.getAnnotation(annotationCls));
    }

    /**
     * Gets the declared field of the class
     * @param cls The class to search
     * @param fieldName The name of the field to find
     * @return An optional wrapper of the field
     */
    public static Optional<Field> GetDeclaredField(Class<?> cls, String fieldName) {
        try {
            Field declaredField = cls.getDeclaredField(fieldName);
            return Optional.ofNullable(declaredField);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * GetDeclaredField but searches through the inheritance tree
     * @param cls The derived class to search
     * @param fieldName The name of the field to find
     * @return An optional wrapper of the field
     */
    public static Optional<Field> GetField(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getField(fieldName);
            return Optional.ofNullable(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static List<Field> GetDeclaredFieldsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationCls) {
        List<Field> annotatedFields = new ArrayList<>();
        AddFields(annotatedFields, annotationCls, cls.getDeclaredFields());
        return annotatedFields;
    }

    public static List<Field> GetFieldsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationCls) {
        List<Field> annotatedFields = new ArrayList<>();
        AddFields(annotatedFields, annotationCls, cls.getFields());
        return annotatedFields;
    }

    private static void AddFields(List<Field> fields, Class<? extends Annotation> annotationCls, Field... clsFields) {
        for (Field field : clsFields) {
            if(field.isAnnotationPresent(annotationCls))
                fields.add(field);
        }
    }

    public static Optional<Object> GetStatic(Field field) {
        return Get(null, field, Object.class);
    }

    public static <T> Optional<T> GetStatic(Field field, Class<T> type) {
        return Get(null, field, type);
    }

    public static Optional<Object> Get(Object owner, Field field) {
        return Get(owner, field, Object.class);
    }

    public static <T> Optional<T> Get(Object owner, Field field, Class<T> type) {
        try {
            Object o;
            if(owner instanceof ObservableValue)
                o = ((ObservableValue) owner).GetValue();
            else o = field.get(owner);
            if(o != null && type.isInstance(o))
                return Optional.of((T) o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T> void SetStatic(Field field, T value) {
        Set(null, field, value);
    }

    public static <T> void Set(Object owner, Field field, T value) {
        Object v = value;
        if(value instanceof ObservableValue)
            v = ((ObservableValue) value).GetValue();
        try {
            if(owner instanceof ObservableValue) {
                ((ObservableValue)owner).SetValue(v);
            }else field.set(owner, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T, S extends T, D extends T> void CopyDeclaredMembers(S source, D target, Class<T> type) throws IllegalAccessException {
        Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Object o = declaredField.get(source);
            declaredField.set(target, o);
        }
    }


}
