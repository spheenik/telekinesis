package telekinesis.util;

public class ClassUtil {

    public static final String packageRelativeClassName(Object o) {
        return packageRelativeName(o.getClass());
    }

    public static final String packageRelativeName(Class<?> clazz) {
        int packageLength = clazz.getPackage().getName().length();
        String clazzName = clazz.getName();
        return packageLength != 0 ? clazzName.substring(packageLength + 1) : clazzName;
    }

}
