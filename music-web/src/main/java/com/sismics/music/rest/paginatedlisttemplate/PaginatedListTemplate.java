package com.sismics.music.rest.paginatedlisttemplate;

import com.sismics.music.core.util.dbi.PaginatedList;

public interface PaginatedListTemplate<T> {
	public PaginatedList<T> getPaginatedList(Integer limit,Integer offset,Integer sortColumn,Boolean asc,String userId);
}
