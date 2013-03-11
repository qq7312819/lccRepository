package com.fcapp.beautyimage.model;

public class HomePopup {
	private int  id;
	private String title;
	private String img_url;
	private String click_url;
	private String click_count;
	private String ad_source;
	private String ad_type;
	private String delay;
	private String width;
	private String height;
	private String is_online;
	private String created_at;
	private String updated_at;
	private String _id;
	private String local_dir;
	public String getLocal_dir() {
		return local_dir;
	}
	public void setLocal_dir(String local_dir) {
		this.local_dir = local_dir;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public String getClick_url() {
		return click_url;
	}
	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}
	public String getClick_count() {
		return click_count;
	}
	public void setClick_count(String click_count) {
		this.click_count = click_count;
	}
	public String getAd_source() {
		return ad_source;
	}
	public void setAd_source(String ad_source) {
		this.ad_source = ad_source;
	}
	public String getAd_type() {
		return ad_type;
	}
	public void setAd_type(String ad_type) {
		this.ad_type = ad_type;
	}
	public String getDelay() {
		return delay;
	}
	public void setDelay(String delay) {
		this.delay = delay;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getIs_online() {
		return is_online;
	}
	public void setIs_online(String is_online) {
		this.is_online = is_online;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	@Override
	public String toString() {
		return "HomePopup [id=" + id + ", title=" + title + ", img_url="
				+ img_url + ", click_url=" + click_url + ", click_count="
				+ click_count + ", ad_source=" + ad_source + ", ad_type="
				+ ad_type + ", delay=" + delay + ", width=" + width
				+ ", height=" + height + ", is_online=" + is_online
				+ ", created_at=" + created_at + ", updated_at=" + updated_at
				+ ", _id=" + _id + "]";
	}
	
	
}
