package com.lhd.service;

import java.util.List;

import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.po.UserInfo;
import com.lhd.entity.vo.PaginationResultVO;
import org.apache.catalina.User;


/**
 * 用户信息表 业务接口
 */
public interface UserInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserInfo bean,UserInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserInfoQuery param);

	/**
	 * 根据UserId查询对象
	 */
	UserInfo getUserInfoByUserId(String userId);


	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoByUserId(UserInfo bean,String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);


	/**
	 * 根据Email查询对象
	 */
	UserInfo getUserInfoByEmail(String email);


	/**
	 * 根据Email修改
	 */
	Integer updateUserInfoByEmail(UserInfo bean,String email);


	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);


	/**
	 * 根据NickName查询对象
	 */
	UserInfo getUserInfoByNickName(String nickName);


	/**
	 * 根据NickName修改
	 */
	Integer updateUserInfoByNickName(UserInfo bean,String nickName);


	/**
	 * 根据NickName删除
	 */
	Integer deleteUserInfoByNickName(String nickName);

	/**
	 * 注册
	 * @param
	 * @return
	 * @author liuhd
	 * 2024/12/5 12:19
	 */

	void register(String nickName,String email,String registerPassword);

	TokenUserInfoDto login(String email, String password, String ip);
	/**
	 * @description: 获取主页用户信息
	 * @param currentUserId 当前用户id
	 * @param userId 被访问的用户id
	 * @return com.lhd.entity.po.UserInfo
	 * @author liuhd
	 * 2025/1/11 13:31
	 */

	UserInfo getUserDetailInfo(String currentUserId,String userId);

	/**
	 * @description: 更新用户信息
	 * @param userInfo
	 * @param tokenUserInfoDto
	 * @return
	 * @author liuhd
	 * 2025/1/11 14:04
	 */

	void updateUserInfo(UserInfo userInfo,TokenUserInfoDto tokenUserInfoDto);
}