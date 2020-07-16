package com.tokensigning.common;

/**
* Comment: object define a comment in pdf file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class Comment {
	public Comment() { }

    public int page ;
    public int llx ;
    public int lly ;
    public int urx ;
    public int ury ;
    public String 	Description ;
    public Boolean 	OnlyDescription ;
    public Boolean 	commentItalic = false;
    public int 		commentFontSize = 14;
    
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLlx() {
		return llx;
	}
	public void setLlx(int llx) {
		this.llx = llx;
	}
	public int getLly() {
		return lly;
	}
	public void setLly(int lly) {
		this.lly = lly;
	}
	public int getUrx() {
		return urx;
	}
	public void setUrx(int urx) {
		this.urx = urx;
	}
	public int getUry() {
		return ury;
	}
	public void setUry(int ury) {
		this.ury = ury;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Boolean getOnlyDescription() {
		return OnlyDescription;
	}
	public void setOnlyDescription(Boolean onlyDescription) {
		OnlyDescription = onlyDescription;
	}
	public Boolean getCommentItalic() {
		return commentItalic;
	}
	public void setCommentItalic(Boolean commentItalic) {
		this.commentItalic = commentItalic;
	}
	public int getCommentFontSize() {
		return commentFontSize;
	}
	public void setCommentFontSize(int commentFontSize) {
		this.commentFontSize = commentFontSize;
	}
    
    
}
