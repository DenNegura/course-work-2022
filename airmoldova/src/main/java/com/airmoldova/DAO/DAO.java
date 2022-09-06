package com.airmoldova.DAO;

import java.util.Collection;

public interface DAO<Entity, Key> {
    void create(Entity entity);
    void update(Entity entity);
    Entity read(Key entity);
    void delete(Entity entity);
}
