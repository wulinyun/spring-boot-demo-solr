/**
 * <p>Title: SolrController.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.landasoft.com</p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:06:41 
 * @version 1.0  
 */
package com.landasoft.mas.demo.solr.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.landasoft.mas.demo.solr.bean.UserBean;

/**
 * <p>Title: SolrController</p>  
 * <p>Description: </p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:06:41
 */
@RestController
@RequestMapping("/solr")
public class SolrController {
	private final static Log logger = LogFactory.getLog(SolrController.class);
	@Autowired
	private SolrClient solrClient;
	/**
	 * 
	 * <p>Title: insert</p>  
	 * <p>Description: 增</p>  
	 * @param userBean
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@PostMapping("/insert")
	public String insert(UserBean userBean) throws SolrServerException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String dateString = sdf.format(new Date());
		try {
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("id", dateString);
			doc.setField("name", userBean.getName());
			doc.setField("password", userBean.getPassword());
			/**
			 * 如果 spring.data.solr.host 里面配置到 core了, 那么这里就不需要传 new_core 这个参数 下面都是一样的 即
			 * client.commit()
			 */
			solrClient.add("new_core", doc);
			solrClient.commit("new_core");
			return dateString;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("插入异常", e);
		}
		return "error";
	}
	/**
	 * 
	 * <p>Title: getDocumentById</p>  
	 * <p>Description: 查id</p>  
	 * @param id
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@GetMapping("/get/{id}")
	public String getDocumentById(@PathVariable String id) throws SolrServerException, IOException {
		SolrDocument document = solrClient.getById("new_core", id);
		logger.info(document);
		return document.toString();
	}
	/**
	 * 
	 * <p>Title: getAll</p>  
	 * <p>Description: 获取全部数据</p>  
	 * @return
	 */
	@GetMapping("/get/all")
	public Map<String,Object> getAll(){
		Map<String,Object> map = new HashMap<String,Object>();
		//solrClient.getById(id, params)
		return map;
	}
	/**
	 * 
	 * <p>Title: deleteDocumentById</p>  
	 * <p>Description: 删id</p>  
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	public String deleteDocumentById(@PathVariable String id) {
		try {
			solrClient.deleteById("new_core", id);
			solrClient.commit("new_core");
			return id;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
	/**
	 * 
	 * <p>Title: deleteAll</p>  
	 * <p>Description: 删所有</p>  
	 * @return
	 */
	@DeleteMapping("/deleteAll")
	public String deleteAll() {
		try {
			solrClient.deleteByQuery("new_core", "*:*");
			solrClient.commit("new_core");
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
	/**
	 * 
	 * <p>Title: update</p>  
	 * <p>Description: 改</p>  
	 * @param userBean
	 * @return
	 */
	@PutMapping("/update")
	public String update(UserBean userBean) {
		try {
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("id", userBean.getId());
			doc.setField("name", userBean.getName());
			doc.setField("password", userBean.getPassword());
			solrClient.add("new_core", doc);
			solrClient.commit("new_core");
			return doc.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
	/**
	 * 
	 * <p>Title: select</p>  
	 * <p>Description: 查关键字，高亮，分页</p>  
	 * @param q 查询条件
	 * @param page 页码
	 * @param size 页大小
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@GetMapping("/select/{q}/{page}/{size}")
	public Map<String,Object> select(@PathVariable String q,@PathVariable Integer page,@PathVariable Integer size) throws SolrServerException, IOException{
		SolrQuery params = new SolrQuery();
		//查询条件
		params.set("q", q);
		//排序
		params.addSort("id", SolrQuery.ORDER.desc);
		//分页
		params.setStart(page);
		params.setRows(size);
		//默认域
		params.set("df", "name");
		//只查询指定域
		params.set("fl", "id,name");
		//开启高亮
		params.setHighlight(true);
		//设置前缀
		params.setHighlightSimplePre("<span style='color:red'>");
		//设置后缀
		params.setHighlightSimplePost("</span>");
		//solr数据库是new_core
		QueryResponse queryResponse = solrClient.query("new_core", params);
		SolrDocumentList results = queryResponse.getResults();
		//数量，分页用
		long total = results.getNumFound();
		//获取高亮显示的结果，高亮显示的结果和查询结果是分开放的
		Map<String,Map<String,List<String>>> hightLight = queryResponse.getHighlighting();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("total", total);
		map.put("data", hightLight);
		return map;
		
	}
}
