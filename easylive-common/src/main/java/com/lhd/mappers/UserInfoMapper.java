package com.lhd.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户信息表 数据库操作接口
 */
public interface UserInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据UserId更新
	 */
	 Integer updateByUserId(@Param("bean") T t,@Param("userId") String userId);


	/**
	 * 根据UserId删除
	 */
	 Integer deleteByUserId(@Param("userId") String userId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


	/**
	 * 根据Email更新
	 */
	 Integer updateByEmail(@Param("bean") T t,@Param("email") String email);


	/**
	 * 根据Email删除
	 */
	 Integer deleteByEmail(@Param("email") String email);


	/**
	 * 根据Email获取对象
	 */
	 T selectByEmail(@Param("email") String email);


	/**
	 * 根据NickName更新
	 */
	 Integer updateByNickName(@Param("bean") T t,@Param("nickName") String nickName);


	/**
	 * 根据NickName删除
	 */
	 Integer deleteByNickName(@Param("nickName") String nickName);


	/**
	 * 根据NickName获取对象
	 */
	 T selectByNickName(@Param("nickName") String nickName);

	/**
	 * @description: 更新用户硬币数
	 * @param userId 用户id
	 * @param changeCount 改变数量
	 * @return 0：硬币为负数或用户不存在 1：正确更新
	 * @author liuhd
	 * 2024/12/13 0:35
	 */

	Integer updateCoinCountInfo(@Param("userId") String userId,@Param("changeCount") Integer changeCount);


}
