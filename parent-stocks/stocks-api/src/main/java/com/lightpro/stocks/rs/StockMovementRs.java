package com.lightpro.stocks.rs;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.stocks.vm.StockMovementVm;
import com.stocks.domains.api.StockMovements;

@Path("/stock-movement")
public class StockMovementRs extends StocksBaseRs {
	
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						StockMovements container = stocks().stockMovements();
						
						List<StockMovementVm> itemsVm = container.find(page, pageSize, filter).stream()
																 .map(m -> new StockMovementVm(m))
																 .collect(Collectors.toList());
													
						int count = container.totalCount(filter);
						PaginationSet<StockMovementVm> pagedSet = new PaginationSet<StockMovementVm>(itemsVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
}