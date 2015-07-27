package com.kxm.kcgl.view;

/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hyjd.frame.psm.datamodel.PaginationDataModel;
import com.hyjd.frame.psm.utils.MsgTool;
import com.kxm.kcgl.domain.User;
import com.kxm.kcgl.service.UserService;

@Component
@Scope("view")
public class UserView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UserService userService;

	private User condition = new User();

	private PaginationDataModel<User> userDataModel;

	private User user = new User();

	@PostConstruct
	public void init() {
		queryUsers();
	}

	public void queryUsers() {
		userDataModel = new PaginationDataModel<User>(
				"com.kxm.kcgl.mapper.UserMapper.selectSelective", condition);
	}

	public void addUser(ActionEvent e) {
		boolean isOk = userService.insertUser(user);
		if (isOk) {
			queryUsers();
			MsgTool.addInfoMsg("success");
		} else {
			MsgTool.addErrorMsg("error");
		}
	}

	public PaginationDataModel<User> getuserDataModel() {
		return userDataModel;
	}

	public void setUserDataModel(PaginationDataModel<User> userDataModel) {
		this.userDataModel = userDataModel;
	}

	public User getCondition() {
		return condition;
	}

	public void setCondition(User condition) {
		this.condition = condition;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PaginationDataModel<User> getUserDataModel() {
		return userDataModel;
	}
}