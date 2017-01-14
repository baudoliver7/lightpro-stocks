package com.stocks.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.Recordable;

public interface ArticlePlanning extends Recordable<UUID> {
	Location location() throws IOException;
	Article article() throws IOException;
	int maximumStock() throws IOException;
	int safetyStock() throws IOException;
	int minimumStock() throws IOException;
	
	void update(int maximumStock, int safetyStock, int minimumStock) throws IOException;
}