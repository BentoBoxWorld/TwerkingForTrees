package world.bentobox.twerk;

public class WhiteBox {
    public static void setInternalState(Class<?> targetClass, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set static field '" + fieldName + "' on class " + targetClass.getName(), e);
        }
    }
}
