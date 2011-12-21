/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.utils;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;


/** 
 * An object to assist organizing a collection of objects into pages, and the appropriate indexing and
 * visualization of the pages themselves.
 * 
 * @author Ferdinando Villa
 *
 */
public class Pager {
	
	int totalItems;
	int itemsPerPage;
	int nPagesToShow;
	int currentItem;
	int currentPage;
	int currentItemOffset;
	int totalPages;
	int itemsOnLastPage;
	
	private IQueryResult queryResult = null;
	
	/*
	 * recalculate all parameters based on the current item index
	 */
	private void calcBoundaries() throws ThinklabException {
		
		int formerPage = currentPage;
		
		/* ensure that the current item is not out of range */
		if (currentItem < 0 || totalItems == 0)
			currentItem = 0;
		else if (currentItem >= totalItems)
			currentItem = totalItems - 1;
		
		/* determine the current page shown */
		int cpg = currentItem / itemsPerPage;
		currentItemOffset = currentItem - cpg*itemsPerPage;
		
		totalPages = totalItems/itemsPerPage;
		itemsOnLastPage = totalItems - totalPages*itemsPerPage;
		
		if (itemsOnLastPage > 0)
			totalPages ++;
		else
			itemsOnLastPage = itemsPerPage;
		
		/* recalc current page */
		currentPage = cpg;
		
		/* 
		 * if we have a query object and we switched page, ask for the proper
		 * results.
		 */
		if (currentPage != formerPage && queryResult != null) {
			
			queryResult.moveTo(currentItem, itemsPerPage);
			
		}
		
	}
	
	/**
	 * 
	 * @param totalItems the total number of items in the collection
	 * @param itemsPerPage the number of items shown in one page
	 * @param nPagesToShow the number of pages that we want to show in a pager index at one time 
	 * 		(using ellipsis for previous and next pages).
	 * @throws ThinklabException 
	 */
	public Pager(int totalItems, int itemsPerPage, int nPagesToShow) throws ThinklabException {
		

		this.currentPage = 0;
		this.totalItems = totalItems;
		this.itemsPerPage = itemsPerPage;
		this.nPagesToShow = nPagesToShow;
		setCurrentItem(0,0);
	}
	
	/**
	 * Associate the pager with a query result. If this constructor is used, any
	 * switch of the current page will cause the correspondent results to be 
	 * loaded automatically from the queriable associated with the results object.
	 * 
	 * @param queryResult
	 * @param itemsPerPage
	 * @param nPagesToShow
	 * @throws ThinklabException 
	 */
	public Pager(IQueryResult queryResult, int itemsPerPage, int nPagesToShow) throws ThinklabException {
		
		this.currentItem = 0;
		this.currentPage = 0;
		this.totalItems =  queryResult.getTotalResultCount();
		this.queryResult = queryResult;
		this.itemsPerPage = itemsPerPage;
		this.nPagesToShow = nPagesToShow;
		setCurrentItem(0,0);
	}
	
	/**
	 * Return the total number of results in the query (which may have retrieved
	 * only some of them).
	 * @return
	 */
	public int getTotalItemsCount() {
		return totalItems;
	}
	
	/*
	 * set the current item index, adjust it to be within boundaries if necessary and
	 * set the remaining parameters.
	 */
	public void setCurrentItem(int index, int base) throws ThinklabException {
		currentItem = index - base;
		calcBoundaries();
	}
	
	public int getCurrentItem(int base) {
		return currentItem + base;
	}
	
	public int getNItemsOnPage(int pageIndex, int base) {
		return 
			pageIndex == getLastPage(base) ? 
				itemsOnLastPage : 
				itemsPerPage;
	}
	
	public int getNItemsOnCurrentPage() {
		return getNItemsOnPage(currentPage,0);
	}
	
	/**
	 * Get the number of pages.
	 * Just a synonym for the 1-based index of the last page.
	 */
	public int getTotalPagesCount() {
		return	getLastPage(1);
	}
	
	/**
	 * Set the current page. The current item becomes the first item in the page.
	 * @param pageIndex the page number (starting at 1).
	 * @throws ThinklabException 
	 */
	public void setCurrentPage(int pageIndex, int base) throws ThinklabException {
		
		pageIndex -= base;
		currentItem = pageIndex * itemsPerPage;
		calcBoundaries();
	}
	
	/**
	 * The page that shows the current item. Starts at base.
	 * @return
	 */
	public int getCurrentPage(int base) {
		return currentPage + base;
	}
	
	/**
	 * Last possible page number, which may be shown in the pager or not according to
	 * how many items we want to show in the pager. Page numbering starts at 1.
	 * @return
	 */
	public int getLastPage(int base) {
		return totalItems <= 0 ? -1 : (totalPages -1 + base);
	}
	

	/**
	 * First page that we want to link to in the pager. First page returns base. No pages returns -1.
	 * @return
	 */
	public int getFirstPageInPager(int base) {

		int pofs = totalPages/currentPage;
		return totalItems == 0 ? -1 : pofs+base;
	}

	/**
	 * Last page that we want to link to in the pager.
	 * @return the page number starting from base, ready for display. Returns -1 if there are no pages to display.
	 */
	public int getLastPageInPager(int base) {
		
		int ret = getFirstPageInPager(base) + nPagesToShow - 1;
		if (ret > getLastPage(base)) {
			ret = getLastPage(base);
		}
		return ret;
	}
	
	public int getFirstItemIndexInCurrentPage(int base) {
		return 
			(getCurrentPage(0) * itemsPerPage) + base;
	}
	
	public int getLastItemIndexInCurrentPage(int base) {
		
		int lastIfPresent = 
			getFirstItemIndexInCurrentPage(base) + 
			getNItemsOnPage(getCurrentPage(base), base) - 1;
	
		return ((totalItems - 1) > lastIfPresent) ? lastIfPresent : (totalItems - 1);
	}
	
	/**
	 * False if there is only zero or one page to show.
	 * @return
	 */
	public boolean hasPager() {
		return totalPages > 1;
	}

	/**
	 * If the first page in the pager is not 0, we need to show an ellipsis.
	 * @return
	 */
	public boolean hasLeftEllipsis() {
		return getFirstPageInPager(0) > 0;
	}

	/**
	 * If the last page in the pager is not the last possible page, we need to show an ellipsis.
	 * @return
	 */
	public boolean hasRightEllipsis() {
		return getLastPageInPager(0) < getLastPage(0);
	}
}
