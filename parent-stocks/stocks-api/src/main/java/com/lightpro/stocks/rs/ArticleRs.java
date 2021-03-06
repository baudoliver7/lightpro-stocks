package com.lightpro.stocks.rs;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infrastructure.core.PaginationSet;
import com.lightpro.stocks.cmd.ArticleEdited;
import com.lightpro.stocks.cmd.ArticlePlanningEdited;
import com.lightpro.stocks.vm.ArticlePlanningVm;
import com.lightpro.stocks.vm.ArticleVm;
import com.securities.api.Secured;
import com.stocks.domains.api.Article;
import com.stocks.domains.api.ArticleFamily;
import com.stocks.domains.api.ArticlePlanning;
import com.stocks.domains.api.Articles;

@Path("/article")
public class ArticleRs extends StocksBaseRs {
	
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAll() throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ArticleVm> items = stocks().articles().all()
													 .stream()
											 		 .map(m -> new ArticleVm(m))
											 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
	
	@GET
	@Secured
	@Path("/{id}/planning")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPlanning(@PathParam("id") final UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						List<ArticlePlanningVm> items = stocks().articles().get(id).plannings().all()
								 .stream()
						 		 .map(m -> new ArticlePlanningVm(m))
						 		 .collect(Collectors.toList());

						return Response.ok(items).build();
					}
				});			
	}
		
	@GET
	@Secured
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	public Response search( @QueryParam("page") int page, 
							@QueryParam("pageSize") int pageSize, 
							@QueryParam("filter") String filter,
							@QueryParam("familyId") UUID familyId) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ArticleFamily family = stocks().articleFamilies().build(familyId);
						Articles articles = stocks().articles().of(family);
						
						List<ArticleVm> articlesVm = articles.find(page, pageSize, filter).stream()
															 .map(m -> new ArticleVm(m))
															 .collect(Collectors.toList());
													
						long count = articles.count(filter);
						PaginationSet<ArticleVm> pagedSet = new PaginationSet<ArticleVm>(articlesVm, page, count);
						
						return Response.ok(pagedSet).build();
					}
				});	
				
	}
	
	@GET
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getSingle(@PathParam("id") UUID id) throws IOException {	
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ArticleVm item = new ArticleVm(stocks().articles().get(id));

						return Response.ok(item).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public Response add(final ArticleEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						ArticleFamily family = stocks().articleFamilies().get(cmd.familyId());
						family.articles().add(cmd.name(), cmd.internalReference(), cmd.barCode(), cmd.quantity(), cmd.cost(), cmd.description(), cmd.emballage());
						
						log.info(String.format("Cr�ation de l'article %s", cmd.name())); 						
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@POST
	@Secured
	@Path("/{id}/planning")
	@Produces({MediaType.APPLICATION_JSON})
	public Response setPlanning(@PathParam("id") final UUID id, final List<ArticlePlanningEdited> plannings) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Article article = stocks().articles().get(id);
						
						for (ArticlePlanningEdited item : plannings) {
							ArticlePlanning planning = article.plannings().get(item.id());
							planning.update(item.maximumStock(), item.safetyStock(), item.minimumStock());
						}
						
						log.info(String.format("Mise � jour des donn�es de plannification de l'article %s", article.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@PUT
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response update(@PathParam("id") final UUID id, final ArticleEdited cmd) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Article article = stocks().articles().get(cmd.id());
						article.update(cmd.name(), cmd.internalReference(), cmd.barCode(), cmd.quantity(), cmd.cost(), cmd.description(), cmd.emballage());
						
						ArticleFamily newFamily = stocks().articleFamilies().build(cmd.familyId());
						article.changeFamily(newFamily);
						
						log.info(String.format("Mise � jour � des donn�es de l'article %s", cmd.name()));
						return Response.status(Response.Status.OK).build();
					}
				});		
	}
	
	@DELETE
	@Secured
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response delete(@PathParam("id") final UUID id) throws IOException {
		
		return createHttpResponse(
				new Callable<Response>(){
					@Override
					public Response call() throws IOException {
						
						Article article = stocks().articles().get(id);
						String name = article.name();
						stocks().articles().delete(article);
						
						log.info(String.format("Suppression de l'article %s", name));
						return Response.status(Response.Status.OK).build();
					}
				});	
	}
}
