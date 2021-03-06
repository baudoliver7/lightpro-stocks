package com.stocks.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;

public interface Warehouses extends AdvancedQueryable<Warehouse, UUID>, Updatable<Warehouse> {
	Warehouse add(String name, String shortName) throws IOException;
}
