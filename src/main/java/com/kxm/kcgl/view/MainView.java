package com.kxm.kcgl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hyjd.frame.psm.base.LoginSession;
import com.hyjd.frame.psm.datamodel.PaginationDataModel;
import com.hyjd.frame.psm.utils.MsgTool;
import com.kxm.kcgl.LogicException;
import com.kxm.kcgl.domain.PreProductOut;
import com.kxm.kcgl.domain.Product;
import com.kxm.kcgl.domain.ProductQueryTimes;
import com.kxm.kcgl.domain.Stock;
import com.kxm.kcgl.domain.User;
import com.kxm.kcgl.service.PreProductOutService;
import com.kxm.kcgl.service.ProductQueryTimesService;
import com.kxm.kcgl.service.ProductService;
import com.kxm.kcgl.service.StockService;

@Component
@Scope("view")
public class MainView implements Serializable {
	private static final long serialVersionUID = -3617720788091260172L;

	@Autowired
	private LoginSession loginSession;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductQueryTimesService productQueryTimesService;

	@Autowired
	private StockService stockService;

	@Autowired
	private PreProductOutService preProductOutService;

	private PreProductOut preProductOut = new PreProductOut();

	private Stock selectedStock = new Stock();

	private Product selectedProduct = new Product();

	private PaginationDataModel<Stock> stockModel;

	@PostConstruct
	public void init() {
		initStockList();
	}

	public List<Product> completeProduct(String keywords) {
		List<Product> list = new ArrayList<Product>();
		if (StringUtils.isEmpty(keywords)) {
			initStockList();
		}
		list = productService.search(keywords);
		return list;
	}

	public void onItemSelect() {
		// 插入查询次数表
		ProductQueryTimes pqt = new ProductQueryTimes();
		pqt.setProductId(selectedProduct.getId());
		User user = (User) loginSession.getSesionObj();
		pqt.setCreateUserId(user.getId());
		productQueryTimesService.insert(pqt);

		initStockList();
	}

	private void initStockList() {
		Stock condition = new Stock();
		condition.setProductId(selectedProduct.getId());
		stockModel = new PaginationDataModel<Stock>("com.kxm.kcgl.mapper.StockMapper.selectSelective", condition);
	}

	public void showPreProductOut(Integer stockId) {
		Stock condition = new Stock();
		condition.setId(stockId);
		List<Stock> stocks = stockService.selectSelective(condition);
		if (stocks.size() <= 0) {
			MsgTool.addWarningMsg("库存不足");
			return;
		} else {
			selectedStock = stocks.get(0);
		}
	}

	public void preProductOut() {
		try {
			User user = (User) loginSession.getSesionObj();
			preProductOut.setCreateUserId(user.getId());
			boolean isOk = preProductOutService.insert(selectedStock, preProductOut);
			if (isOk) {
				MsgTool.addInfoMsg("预出货成功");
				RequestContext.getCurrentInstance().execute("PF('pre_product_out').hide()");
			} else {
				MsgTool.addWarningMsg("预出货失败");
			}
		} catch (LogicException e) {
			MsgTool.addWarningMsg(e.getMessage());
			e.printStackTrace();
		}
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public PaginationDataModel<Stock> getStockModel() {
		return stockModel;
	}

	public void setStockModel(PaginationDataModel<Stock> stockModel) {
		this.stockModel = stockModel;
	}

	public PreProductOut getPreProductOut() {
		return preProductOut;
	}

	public void setPreProductOut(PreProductOut preProductOut) {
		this.preProductOut = preProductOut;
	}

	public Stock getSelectedStock() {
		return selectedStock;
	}

	public void setSelectedStock(Stock selectedStock) {
		this.selectedStock = selectedStock;
	}
}