package net.alperemre.util;

import net.alperemre.models.BaseModel;
import org.reflections.Reflections;

import java.util.Set;

// discovers all concrete subclasses of BaseModel using the Reflections library.

public class ModelDiscovery {
    public static Set<Class<? extends BaseModel>> findAllModelClasses() {
        // This scans the package net.alperemre.models and subpackages
        Reflections reflections = new Reflections("net.alperemre.models");

        // Return all subtypes of BaseModel
        return reflections.getSubTypesOf(BaseModel.class);
    }
}
