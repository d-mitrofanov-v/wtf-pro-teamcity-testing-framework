package com.teamcity.api.generators;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.unchecked.UncheckedBase;
import com.teamcity.api.spec.Specs;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TestDataStorage {
    private static TestDataStorage testDataStorage;
    private final EnumMap<Endpoint, Set<String>> createdEntities;

    private TestDataStorage() {
        createdEntities = new EnumMap<>(Endpoint.class);
    }

    public static TestDataStorage getStorage() {
        if (testDataStorage == null) {
            testDataStorage = new TestDataStorage();
        }
        return testDataStorage;
    }

    private String getEntityIdOrLocator(BaseModel model) {
        try {
            var idField = model.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            var idFieldValue = Objects.toString(idField.get(model), null);
            idField.setAccessible(false);
            return idFieldValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                var locatorField = model.getClass().getDeclaredField("locator");
                locatorField.setAccessible(true);
                var locatorFieldValue = Objects.toString(locatorField.get(model), null);
                locatorField.setAccessible(false);
                return locatorFieldValue;
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot get id or locator of entity", e);
            }
        }
    }

    public void addCreatedEntity(Endpoint endpoint, String id) {
        createdEntities.computeIfAbsent(endpoint, key -> new HashSet<>()).add(id);
    }

    public void addCreatedEntity(Endpoint endpoint, BaseModel model) {
        addCreatedEntity(endpoint, getEntityIdOrLocator(model));
    }

    public void deleteCreatedEntities() {
        createdEntities.forEach(
                (endpoint, ids) -> ids.forEach(
                        id -> new UncheckedBase(Specs.superUserAuth(), endpoint).delete(id)
                )
        );
        createdEntities.clear();
    }
}
