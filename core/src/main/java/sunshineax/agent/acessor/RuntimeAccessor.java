package sunshineax.agent.acessor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RuntimeAccessor {

    private RuntimeAccessor() {
    }

    private static final Map<Class<?>, ClassAccessor> CACHE = new ConcurrentHashMap<>();

    public static Object read(Object instance, String field) {
        return accessor(instance.getClass())
                .field(field)
                .get(instance);
    }

    public static void write(Object instance, String field, Object value) {
        accessor(instance.getClass())
                .field(field)
                .set(instance, value);
    }

    public static boolean hasField(Class<?> clazz, String field) {
        return accessor(clazz).contains(field);
    }

    public static ClassAccessor accessor(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, (a) ->  RuntimeAccessor.createAccessor(clazz));
    }

    private static ClassAccessor createAccessor(Class<?> clazz) {

        Map<String, FieldAccessor> fields = new ConcurrentHashMap<>();

        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(current, MethodHandles.lookup());

                for (Field field : current.getDeclaredFields()) {
                    try {

                        MethodHandle getter = lookup.unreflectGetter(field);
                        MethodHandle setter = lookup.unreflectSetter(field);

                        fields.put(field.getName(), new FieldAccessor(getter, setter));

                    } catch (Throwable e) {
                        throw new RuntimeException(
                                "Erro ao gerar accessor para "
                                        + current.getName()
                                        + "."
                                        + field.getName(),
                                e
                        );
                    }

                }
                current = current.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return new ClassAccessor(fields);
    }

    public static class  ClassAccessor {

        private final Map<String, FieldAccessor> fields;

        public ClassAccessor(Map<String, FieldAccessor> fields) {
            this.fields = fields;
        }

        public FieldAccessor field(String name) {

            FieldAccessor accessor = fields.get(name);

            if (accessor == null) {
                throw new IllegalArgumentException("Campo não encontrado: " + name);
            }

            return accessor;
        }

        public boolean contains(String name) {
            return fields.containsKey(name);
        }
    }

    public static class FieldAccessor {

        private final MethodHandle getter;
        private final MethodHandle setter;

        public FieldAccessor(MethodHandle getter, MethodHandle setter) {
            this.getter = getter;
            this.setter = setter;
        }

        public Object get(Object instance) {
            try {
                return getter.invoke(instance);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public void set(Object instance, Object value) {
            try {
                setter.invoke(instance, value);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}