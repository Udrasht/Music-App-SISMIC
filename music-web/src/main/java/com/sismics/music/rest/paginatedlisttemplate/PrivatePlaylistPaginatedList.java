package com.sismics.music.rest.paginatedlisttemplate;

import com.sismics.music.core.dao.dbi.PlaylistDao;
import com.sismics.music.core.dao.dbi.criteria.PlaylistCriteria;
import com.sismics.music.core.dao.dbi.dto.PlaylistDto;
import com.sismics.music.core.util.dbi.PaginatedList;
import com.sismics.music.core.util.dbi.PaginatedLists;
import com.sismics.music.core.util.dbi.SortCriteria;

public class PrivatePlaylistPaginatedList implements PaginatedListTemplate<PlaylistDto> {

	@Override
	public PaginatedList<PlaylistDto> getPaginatedList(Integer limit, Integer offset, Integer sortColumn,
			Boolean asc,String userId) {
		PaginatedList<PlaylistDto> paginatedList = PaginatedLists.create(limit,offset);
		SortCriteria sortCriteria = new SortCriteria(sortColumn,asc);
		new PlaylistDao().findByCriteria(paginatedList,
				new PlaylistCriteria()
				.setDefaultPlaylist(false)
				.setPrivacy("private")
				.setUserId(userId),sortCriteria,null);
		
		return paginatedList;
	}


}
