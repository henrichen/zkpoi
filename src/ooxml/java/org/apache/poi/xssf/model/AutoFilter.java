package org.zkoss.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;
import org.zkoss.poi.POIXMLDocumentPart;
import org.zkoss.poi.openxml4j.opc.PackagePart;
import org.zkoss.poi.openxml4j.opc.PackageRelationship;
import org.zkoss.poi.xssf.usermodel.XSSFName;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.XSSFWorkbook;


public class AutoFilter extends POIXMLDocumentPart {
	private CTAutoFilter _autofilter;
	
	private XSSFSheet _sheet;
	
	/**
	 * what value is acceptable? 0:means no filter 1: means there is filter?
	 * maybe it can be just ignored
	 */
	private int filterMode;
	List<FilterColumn> filterColumns;
	
	public List<FilterColumn> getFilterColumns() {
		return filterColumns;
	}

	public class FilterColumn{
		private long colId;
		public long getColId() {
			return colId;
		}

		List<String> filter ;
		
		public List<String> getFilter() {
			return filter;
		}

		FilterColumn(long colId){
			this.colId = colId;
		}
		
		public void addFilterValue(String value){
			if(filter == null){
				filter = new ArrayList<String>();
			}
			filter.add(value);
		}
	}
	
	public List<String> getValuesOfFilter(long col){
		for(FilterColumn fc : filterColumns){
			if(fc.getColId() == col){
				return fc.getFilter();
			}
		}
		return null;
	}
	
	public void addFilterColumn(CTFilterColumn ctFC){
		if(filterColumns == null)
			filterColumns = new ArrayList<FilterColumn>();

		FilterColumn fc = new FilterColumn(ctFC.getColId());

		//Don't use list, because it's 100 times slower than array
//		for(CTFilter ct: ctFC.getFilters().getFilterList()){
//			fc.addFilterValue(ct.getVal());
//		}
		
		CTFilter[] ctFilterArray = ctFC.getFilters().getFilterArray();
		for(int i = 0; i < ctFilterArray.length; i++){
			fc.addFilterValue(ctFilterArray[i].getVal());
		}
		
		filterColumns.add(fc);
	}
	
	public AutoFilter(XSSFSheet sheet){
		super();
		_autofilter = CTAutoFilter.Factory.newInstance();
		_sheet = sheet;
	}
	
    public AutoFilter(PackagePart part, PackageRelationship rel) throws IOException {
        super(part, rel);
        readFrom(part.getInputStream());
    }
	
    public void readFrom(InputStream is) throws IOException {
        try {
        	_autofilter = CTAutoFilter.Factory.parse(is);
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void writeTo(OutputStream out) throws IOException {
    	_autofilter.save(out, DEFAULT_XML_OPTIONS);
    }

    @Override
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }

    /**
     * 
     * @param colNum
     * @return whether column colNum has filtering
     */
    //TODO:
    public boolean isFilteringCol(int colNum){
    	//if column in autofilter name range, and such column has values to filter
    	//then it's filtering
    	//if such column has values to filter, it's enough
    	if(getValuesOfFilter(colNum) != null){
    		return true;
    	}
    	return false;
    }
}
