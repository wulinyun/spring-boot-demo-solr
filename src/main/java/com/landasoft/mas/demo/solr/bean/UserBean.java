/**
 * <p>Title: UserBean.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.landasoft.com</p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:19:13 
 * @version 1.0  
 */
package com.landasoft.mas.demo.solr.bean;

import java.io.Serializable;

/**
 * <p>Title: UserBean</p>  
 * <p>Description: 实体bean</p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:19:13
 */
public class UserBean implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String password;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
