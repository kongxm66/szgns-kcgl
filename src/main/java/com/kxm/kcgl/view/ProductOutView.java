package com.kxm.kcgl.view;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hyjd.frame.psm.base.LoginSession;
import com.hyjd.frame.psm.datamodel.PaginationDataModel;
import com.hyjd.frame.psm.utils.MsgTool;
import com.kxm.kcgl.LogicException;
import com.kxm.kcgl.domain.Bill;
import com.kxm.kcgl.domain.Product;
import com.kxm.kcgl.domain.ProductOut;
import com.kxm.kcgl.domain.User;
import com.kxm.kcgl.mapper.ProductMapper;
import com.kxm.kcgl.service.ProductOutService;
import com.kxm.kcgl.service.ProductService;

@Component
@Scope("view")
public class ProductOutView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ProductOutService productOutService;
	@Autowired
	private ProductService productService;

	private Bill billCondition = new Bill();
	private ProductOut productOutCondition = new ProductOut();
	
	private PaginationDataModel<Bill> billDataModel;

	private List<ProductOut> tempProductOutList = new LinkedList<ProductOut>();

	private ProductOut productOut = new ProductOut();

	@Autowired
	private LoginSession loginSession;

	@Autowired
	private ProductMapper productMapper;

	private PaginationDataModel<ProductOut> productOutDataModel;

	private Integer custId;

	private String keywords;

	@PostConstruct
	public void init() {
		initBillList();
		initProductOutList();
	}

	public void initBillList() {
		billDataModel = new PaginationDataModel<Bill>("com.kxm.kcgl.mapper.BillMapper.selectSelective", billCondition);
	}

	public void initProductOutList() {
		productOutDataModel = new PaginationDataModel<ProductOut>("com.kxm.kcgl.mapper.ProductOutMapper.selectSelective", productOutCondition);
	}

	public void showBillDetail(Bill bill) {
		Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", true);
        options.put("contentWidth", 1200);
        options.put("includeViewParams", true);
        List<String>  list = new ArrayList<String>();
        Map<String,List<String>> param = new HashMap<String, List<String>>();
        list.add(bill.getId().toString());
        param.put("billId", list);
	    RequestContext.getCurrentInstance().openDialog("bill", options, param);
	}

	public void editExistTemp(Integer productId) {
		User user = (User) loginSession.getSesionObj();
		this.productOut = productOutService.getProductOut(productId, user);
		RequestContext.getCurrentInstance().execute("PF('edit_dlg').show()");
	}

	public void delExistTemp(ProductOut productOut) {
		tempProductOutList.remove(productOut);
	}

	public void addProductOutTmp() {
		if (StringUtils.isEmpty(keywords)) {
			MsgTool.addErrorMsg("请填写产品编号或产品名称");
			return;
		}
		Product condition = new Product();
		condition.setProductName(keywords);
		User user = (User) loginSession.getSesionObj();
		condition.setManufactorId(user.getManufactorId());
		List<Product> products = productService.search(condition);
		
		try {
			productOutService.addProductOut(products, tempProductOutList, user);
		} catch (LogicException e) {
			MsgTool.addWarningMsg(e.getMessage());
		}
	}

	public void saveEditTemp() throws IllegalAccessException, InvocationTargetException {
		for (ProductOut p : tempProductOutList) {
			if (productOut.getProductNo().equals(p.getProductNo())) {
				BeanUtils.copyProperties(p, productOut);
				break;
			}
		}
		RequestContext.getCurrentInstance().execute("PF('edit_dlg').hide()");
	}

	public void productOut() {
		try {
			User user = (User) loginSession.getSesionObj();
			productOutService.productOut(tempProductOutList, user.getId(), custId);
			tempProductOutList.clear();
			MsgTool.addInfoMsg("出货成功");
			RequestContext.getCurrentInstance().execute("PF('product_out_dlg').hide()");
		} catch (LogicException e) {
			MsgTool.addInfoMsg(e.getMessage());
		}
	}
	
	/**
	 * 删除出货单
	 */
	public void rollbackBill(Bill bill){
		productOutService.rollbackBill(bill.getId());
		MsgTool.addInfoMsg("删除成功");
	}

	public ProductOut getProductOut() {
		return productOut;
	}

	public void setProductOut(ProductOut productOut) {
		this.productOut = productOut;
	}

	public PaginationDataModel<Bill> getBillDataModel() {
		return billDataModel;
	}

	public void setBillDataModel(PaginationDataModel<Bill> billDataModel) {
		this.billDataModel = billDataModel;
	}

	public Bill getBillCondition() {
		return billCondition;
	}

	public void setBillCondition(Bill billCondition) {
		this.billCondition = billCondition;
	}

	public List<ProductOut> getTempProductOutList() {
		return tempProductOutList;
	}

	public void setTempProductOutList(List<ProductOut> tempProductOutList) {
		this.tempProductOutList = tempProductOutList;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public PaginationDataModel<ProductOut> getProductOutDataModel() {
		return productOutDataModel;
	}

	public void setProductOutDataModel(PaginationDataModel<ProductOut> productOutDataModel) {
		this.productOutDataModel = productOutDataModel;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}