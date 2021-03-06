package com.lightpro.stocks.cmd;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stocks.domains.api.OperationCategory;

public class OperationTypeEdited {

	private final UUID id;
	private final String name;
	private final UUID defaultSourceLocationId;
	private final UUID defaultDestinationLocationId;
	private final OperationCategory categoryId;
	private final UUID sequenceId;
	private final UUID preparationOpTypeId;
	private final UUID returnOpTypeId;
	
	public OperationTypeEdited(){
		throw new UnsupportedOperationException("#OperationTypeEdited()");
	}
	
	@JsonCreator
	public OperationTypeEdited(@JsonProperty("id") final UUID id,
					    	   @JsonProperty("name") final String name, 
					    	   @JsonProperty("defaultSourceLocationId") final UUID defaultSourceLocationId,
					    	   @JsonProperty("defaultDestinationLocationId") final UUID defaultDestinationLocationId,
					    	   @JsonProperty("categoryId") final int categoryId,
					    	   @JsonProperty("sequenceId") final UUID sequenceId,
					    	   @JsonProperty("preparationOpTypeId") final UUID preparationOpTypeId,
					    	   @JsonProperty("returnOpTypeId") final UUID returnOpTypeId){
		
		this.id = id;
		this.name = name;
		this.defaultSourceLocationId = defaultSourceLocationId;
		this.defaultDestinationLocationId = defaultDestinationLocationId;
		this.categoryId = OperationCategory.get(categoryId);
		this.sequenceId = sequenceId;
		this.preparationOpTypeId = preparationOpTypeId;
		this.returnOpTypeId = returnOpTypeId;
	}
	
	public UUID id(){
		return id;
	}
	
	public String name(){
		return name;
	}
	
	public UUID defaultSourceLocationId(){
		return defaultSourceLocationId;
	}
	
	public UUID defaultDestinationLocationId(){
		return defaultDestinationLocationId;
	}
	
	public OperationCategory category(){
		return categoryId;
	}
	
	public UUID sequenceId(){
		return sequenceId;
	}
	
	public UUID preparationOpTypeId(){
		return preparationOpTypeId;
	}
	
	public UUID returnOpTypeId(){
		return returnOpTypeId;
	}
}
