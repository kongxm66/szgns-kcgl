package com.kxm.kcgl.mapper;

import java.util.List;

import com.kxm.kcgl.domain.Product;

/**
 *
 * @author kongxm
 * @date 2015 2015年7月26日 上午9:39:55
 */
public interface ProductMapper {
	List<Product> selectSelective(Product record);
	
	/**
	 * 可以出货的产品
	 * @param product
	 * @return
	 */
	Product selectCanOutProduct(Product product);

	int countBySelective(Product record);

	int insert(Product record);

	List<Product> search(Product product);

	int update(Product record);
}
