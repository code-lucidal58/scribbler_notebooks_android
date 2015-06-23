package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 22-Jun-15.
 */
public class DealListResponse {
    ArrayList<Deal> dealList=null;
    int dealCount=0;
    int pageCount=0;
    int currentPage=1;

    public DealListResponse(ArrayList<Deal> dealList, int pageCount, int dealCount, int currentPage) {
        this.dealList = dealList;
        this.pageCount = pageCount;
        this.dealCount = dealCount;
        this.currentPage = currentPage;
    }

    public ArrayList<Deal> getDealList() {
        return dealList;
    }

    public void setDealList(ArrayList<Deal> dealList) {
        this.dealList = dealList;
    }

    public int getDealCount() {
        return dealCount;
    }

    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
