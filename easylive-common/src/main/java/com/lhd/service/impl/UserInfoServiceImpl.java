package com.lhd.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.UserSexEnum;
import com.lhd.entity.enums.UserStatuseEnum;
import com.lhd.exception.BusinessException;
import com.lhd.utils.CopyTools;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.po.UserInfo;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.UserInfoMapper;
import com.lhd.service.UserInfoService;
import com.lhd.utils.StringTools;


/**
 * 用户信息表 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private RedisComponent redisComponent;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据NickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName修改
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}
	
	/**
	 * 用户注册
	 * @param 
	 * @return 
	 * @author liuhd
	 * 2024/12/5 12:19
	 */
	
    @Override
    public void register(String nickName, String email, String registerPassword) {
		//判断邮箱是否存在
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo != null){
			throw new BusinessException("用户邮箱已经存在");
		}
		// 判断用户名是否存在
		UserInfo userInfo1 = userInfoMapper.selectByNickName(nickName);
		if (userInfo1 != null){
			throw new BusinessException("昵称已经存在");
		}
		// 将用户插入数据库
		UserInfo user = new UserInfo();
		String userId = StringTools.getRandomNumberString(Constants.LENGTH_10);
		user.setUserId(userId);
		user.setEmail(email);
		user.setNickName(nickName);
		String passwordByMd5 = StringTools.encodeByMd5(registerPassword);
		user.setPassword(passwordByMd5);
		user.setJoinTime(new Date());
		user.setSex(UserSexEnum.SECRECY.getType());
		user.setStatus(UserStatuseEnum.ENABLE.getStatus());
		user.setTheme(Constants.ONE);

		//添加硬币
		user.setCurrentCoinCount(Constants.TEN);
		user.setTotalCoinCount(Constants.TEN);

		userInfoMapper.insert(user);
	}
	
	/**
	 * 用户登录
	 *
	 * @param
	 * @return
	 * @author liuhd
	 * 2024/12/5 15:39
	 */
	
	@Override
	public TokenUserInfoDto login(String email, String password, String ip) {
		// 拿到账号信息
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		// 如果账号不存在或密码错误
		if (userInfo == null || !userInfo.getPassword().equals(password)){
			throw new BusinessException("账号不存在或密码有误");
		}
		// 如果状态为异常
		if (userInfo.getStatus().equals(UserStatuseEnum.DISABLE.getStatus())){
			throw new BusinessException("账号状态异常");
		}
		// 设置此次登录的时间与ip
		UserInfo updateInfo = new UserInfo();
		updateInfo.setLastLoginIp(ip);
		updateInfo.setLastLoginTime(new Date());
		userInfoMapper.updateByUserId(updateInfo,userInfo.getUserId());

		// 将信息存入tokenInfoDto
		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
		// 将tokenInfoDto存入redis
		redisComponent.saveTokenInfo(tokenUserInfoDto);

		return tokenUserInfoDto;
	}
}