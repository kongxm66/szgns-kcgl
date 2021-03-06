package com.kxm.kcgl.view;

/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hyjd.frame.psm.base.LoginSession;
import com.hyjd.frame.psm.datamodel.PaginationDataModel;
import com.hyjd.frame.psm.utils.MsgTool;
import com.kxm.kcgl.LogicException;
import com.kxm.kcgl.domain.User;
import com.kxm.kcgl.service.UserService;

@Component
@Scope("view")
public class UserView implements Serializable {

	private static final long serialVersionUID = 1L;
	@Autowired
	private LoginSession loginSession;

	@Autowired
	private UserService userService;

	private User condition = new User();

	private PaginationDataModel<User> userDataModel;

	private User user = new User();

	private String oldPassword;
	private String newPassword1;
	private String newPassword2;

	@PostConstruct
	public void init() {
		queryUsers();
	}

	public void changePassword() {
		User user = (User) loginSession.getSesionObj();
		try {
			int size = userService.changePassword(user.getId(), oldPassword,
					newPassword1, newPassword2);
			if (size > 0) {
				MsgTool.addInfoMsg("密码更新成功");
				RequestContext.getCurrentInstance().execute("PF('pwd_dlg').hide()");
			} else {
				MsgTool.addWarningMsg("密码更新失败");
			}
		} catch (LogicException e) {
			MsgTool.addErrorMsg(e.getMessage());
		}

	}

	public void queryUsers() {
		userDataModel = new PaginationDataModel<User>(
				"com.kxm.kcgl.mapper.UserMapper.selectSelective", condition);
	}

	public void initUpdateUser(User u) {
		this.user = u;
	}

	public void addUser(ActionEvent e) {
		boolean isOk = false;
		try {
			// 两个一样，username和realname
			user.setRealname(user.getUsername());
			isOk = userService.insertUser(user);
		} catch (LogicException le) {
			MsgTool.addInfoMsg(le.getMessage());
		}
		if (isOk) {
			queryUsers();
			MsgTool.addInfoMsg("添加成功");
		} else {
			MsgTool.addErrorMsg("添加失败");
		}
	}

	public void updateUser() {
		// 两个一样，username和realname
		user.setRealname(user.getUsername());
		boolean isOk = userService.updateUser(user);
		if (isOk) {
			queryUsers();
			MsgTool.addInfoMsg("更新成功");
		} else {
			MsgTool.addErrorMsg("更新失败");
		}
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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword1() {
		return newPassword1;
	}

	public void setNewPassword1(String newPassword1) {
		this.newPassword1 = newPassword1;
	}

	public String getNewPassword2() {
		return newPassword2;
	}

	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}
}