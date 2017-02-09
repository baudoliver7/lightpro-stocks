package com.stocks.domains.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import com.common.utilities.convert.UUIDConvert;
import com.infrastructure.core.HorodateMetadata;
import com.infrastructure.core.impl.HorodateImpl;
import com.infrastructure.datasource.Base;
import com.infrastructure.datasource.DomainStore;
import com.infrastructure.datasource.DomainsStore;
import com.stocks.domains.api.Operation;
import com.stocks.domains.api.OperationMetadata;
import com.stocks.domains.api.OperationType;
import com.stocks.domains.api.Operations;
import com.stocks.domains.api.Operation.OperationStatut;

public class OperationsByType implements Operations {

	private transient final Base base;
	private final transient OperationMetadata dm;
	private final transient OperationStatut statut;
	private final transient OperationType type;
	private final transient DomainsStore ds;
	
	public OperationsByType(final Base base, final OperationStatut statut, final OperationType type){
		this.base = base;
		this.type = type;
		this.statut = statut;
		this.dm = OperationImpl.dm();
		this.ds = this.base.domainsStore(this.dm);	
	}
	
	@Override
	public List<Operation> all() throws IOException {
		return find(0, 0, "");
	}

	@Override
	public List<Operation> find(String filter) throws IOException {
		return find(0, 0, filter);
	}

	@Override
	public List<Operation> find(int page, int pageSize, String filter) throws IOException {
		List<Operation> values = new ArrayList<Operation>();
		
		HorodateMetadata hm = HorodateImpl.dm();
		String statement;
		
		List<Object> params = new ArrayList<Object>();
		
		if(statut == OperationStatut.NONE) {
			statement = String.format("SELECT %s FROM %s WHERE %s=? AND (%s ILIKE ? OR %s ILIKE ?) ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.operationTypeIdKey(), dm.referenceKey(), dm.documentSourceKey(), hm.dateCreatedKey());
			params.add(this.type.id());
		}
		else {
			statement = String.format("SELECT %s FROM %s WHERE %s=? AND %s=? AND (%s ILIKE ? OR %s ILIKE ?) ORDER BY %s DESC LIMIT ? OFFSET ?", dm.keyName(), dm.domainName(), dm.operationTypeIdKey(), dm.statutIdKey(), dm.referenceKey(), dm.documentSourceKey(), hm.dateCreatedKey());
			params.add(this.type.id());
			params.add(statut.id());
		}		
		
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		if(pageSize > 0){
			params.add(pageSize);
			params.add((page - 1) * pageSize);
		}else{
			params.add(null);
			params.add(0);
		}
		
		List<DomainStore> results = ds.findDs(statement, params);
		for (DomainStore domainStore : results) {
			values.add(new OperationImpl(this.base, UUIDConvert.fromObject(domainStore.key()))); 
		}		
		
		return values;
	}

	@Override
	public int totalCount(String filter) throws IOException {
		List<Object> params = new ArrayList<Object>();
		
		String statement;
		
		if(statut == OperationStatut.NONE) {
			statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s=? AND (%s ILIKE ? OR %s ILIKE ?)", dm.keyName(), dm.domainName(), dm.operationTypeIdKey(), dm.referenceKey(), dm.documentSourceKey());
			params.add(this.type.id());
		}
		else {
			statement = String.format("SELECT COUNT(%s) FROM %s WHERE %s=? AND %s=? AND (%s ILIKE ? OR %s ILIKE ?)", dm.keyName(), dm.domainName(), dm.operationTypeIdKey(), dm.statutIdKey(), dm.referenceKey(), dm.documentSourceKey());
			params.add(this.type.id());
			params.add(statut.id());
		}		
		
		filter = (filter == null) ? "" : filter;
		params.add("%" + filter + "%");
		params.add("%" + filter + "%");
		
		List<Object> results = ds.find(statement, params);
		return Integer.parseInt(results.get(0).toString());		
	}

	@Override
	public Operation get(UUID id) throws IOException {
		Operation op = new OperationImpl(this.base, id);
		
		if(!op.isPresent() || 
		   (op.isPresent() && (statut != OperationStatut.NONE && op.statut() != statut) || 
		   !op.type().isEqual(this.type)))
			throw new NotFoundException("L'article n'a pas �t� trouv� !");
		
		return op;
	}

	@Override
	public void delete(Operation item) throws IOException {
		if(contains(item)) {
			item.movements().deleteAll();
			ds.delete(item.id());
		}
	}

	@Override
	public boolean contains(Operation item) {
		
		try {
			return item.isPresent() && item.statut() == OperationStatut.VALIDE;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;		
	}

	@Override
	public Operation build(UUID id) {
		return new OperationImpl(base, id);
	}

	@Override
	public void deleteAll() throws IOException {
		for (Operation op : all()) {
			delete(op);
		}
	}
}
