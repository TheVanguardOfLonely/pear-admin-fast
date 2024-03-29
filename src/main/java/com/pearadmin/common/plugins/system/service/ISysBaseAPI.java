package com.pearadmin.common.plugins.system.service;

import com.pearadmin.common.plugins.logging.enums.BusinessType;
import com.pearadmin.common.plugins.logging.enums.LoggingType;
import com.pearadmin.common.plugins.system.domain.*;
import java.util.List;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: Songaw
 * @Date:2020年12月1日14:33:12
 * @Version:V1.0
 */
public interface ISysBaseAPI {

	/**
	 * 日志添加
	 * @param title 标题
	 * @param description 描述
	 * @param logType 日志类型(0:操作日志;1:登录日志;2:定时任务)
	 * @param operatetype 操作类型(1:添加;2:修改;3:删除;)
	 */
	void addLog(String title, String description, LoggingType logType, BusinessType operatetype);
	
	/**
	  * 根据用户账号查询用户信息
	 * @param username
	 * @return
	 */
	SysUserModel getUserByName(String username);


	/**
	  * 根据用户id查询用户信息
	 * @param id
	 * @return
	 */
	SysUserModel getUserById(String id);
	
	/**
	 * 通过用户账号查询角色集合
	 * @param username
	 * @return
	 */
	List<SysRoleModel> getRolesByUsername(String username);

	/**
	 * 根据字典code获取可用的字典列表数据
	 * @param typeCode
	 * @return
	 */
	List<SysDictDataModel> selectDictByCode(String typeCode);

	/**
	 * 查询表字典通过查询指定table的 text code key 获取字典值
	 * @param table 表名
	 * @param text label
	 * @param code value
	 * @return
	 */
    List<SysDictDataModel> queryTableDictItemsByCode(String table, String text, String code);


	/**
	 * 查询表字典 通过查询指定table的 text code 获取字典（指定查询条件）
	 * @param table 表名
	 * @param text label
	 * @param code value
	 * @return
	 */
	List<SysDictDataModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql);


	/**
	 * 查询表字典 通过查询指定table的 text code key 获取字典值，包含value
	 * @param table 表名
	 * @param text label
	 * @param code value
	 * @param keyArray values
	 * @return
	 */
	List<SysDictDataModel>queryTableDictByKeys(String table, String text, String code, String[] keyArray);

}
