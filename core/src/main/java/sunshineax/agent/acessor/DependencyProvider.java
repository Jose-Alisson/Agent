package sunshineax.agent.acessor;

import sunshineax.agent.annotations.Inject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyProvider {

    private final Map<Class<?>, List<Object>> dependencias = new ConcurrentHashMap<>();

    public DependencyProvider(Object... dependencies) {
        add(dependencies);
    }

    public void add(Object... dependency) {
        for (Object o : dependency) {
            if (o == null) continue;
            Class<?> clazz = o.getClass();

            List<Object> dependencies = dependencias.put(clazz, new ArrayList<>(List.of(o)));

            if(dependencies != null) {
                dependencies.add(o);
            }

            registrarInterfaces(clazz, o);
            registrarSuperClasses(clazz, o);
        }
    }

    private void registrarInterfaces(Class<?> clazz, Object instance) {
        for (Class<?> iface : clazz.getInterfaces()) {
            List<Object> dependency = dependencias.putIfAbsent(iface, new ArrayList<>(List.of(instance)));

            if(dependency != null) {
                dependency.add(instance);
            }
            registrarInterfaces(iface, instance);
        }
    }

    private void registrarSuperClasses(Class<?> clazz, Object instance) {
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            List<Object> dependency = dependencias.putIfAbsent(superclass, new ArrayList<>(List.of(instance)));

            if(dependency != null) {
                dependency.add(instance);
            }

            registrarInterfaces(superclass, instance);
            superclass = superclass.getSuperclass();
        }
    }

    public void inject(Object... instances) {
        for (Object instance : instances) {
            if (instance != null) {
                inject(instance);
            }
        }
    }

    public <T> T injectAndReturn(T t){
        inject(t);
        return t;
    }

    public List<Object> get(Class<?> clazz) {
        List<Object> dependency = dependencias.get(clazz);
        if (dependency == null || dependency.isEmpty()) {
            throw new IllegalStateException(String.format("Dependency provider for class %s not found", clazz.getName()));
        }
        return dependency;
    }

    public void inject(Object instance) {
        Class<?> clazz = instance.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {

                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> type = field.getType();
                    List<Object> injectValue = dependencias.get(type);

                    if (injectValue == null) {
                        throw new RuntimeException("Nenhuma dependência encontrada para o tipo: " + type.getName() + " para classe: " + clazz.getName());
                    }

                    if(type.equals(List.class)) {
                        RuntimeAccessor.write(instance, field.getName(), injectValue);
                    } else {
                        RuntimeAccessor.write(instance, field.getName(), injectValue.getFirst());
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public void printDependencies(){
        dependencias.forEach((k, v) -> {
            System.out.printf("%s -> %s\n", k.getName(), v);
        });
    }
}