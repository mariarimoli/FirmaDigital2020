package com.tokensigning.common;

/**
* RectangleCustom: define signature rectangle on pdf file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class RectangleCustom {
	public RectangleCustom(float llx, float lly, float urx, float ury)
    {
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }
    public float getLlx() {
		return llx;
	}
	public void setLlx(float llx) {
		this.llx = llx;
	}
	public float getLly() {
		return lly;
	}
	public void setLly(float lly) {
		this.lly = lly;
	}
	public float getUrx() {
		return urx;
	}
	public void setUrx(float urx) {
		this.urx = urx;
	}
	public float getUry() {
		return ury;
	}
	public void setUry(float ury) {
		this.ury = ury;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	public float llx ;
    public float lly ;
    public float urx ;
    public float ury ;
    public int page ;
}
