package com.lhd.mappers;

import com.lhd.entity.dto.CountInfoDto;
import org.apache.ibatis.annotations.Param;

/**
 * 视频信息 数据库操作接口
 */
public interface VideoInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据VideoId更新
	 */
	 Integer updateByVideoId(@Param("bean") T t,@Param("videoId") String videoId);


	/**
	 * 根据VideoId删除
	 */
	 Integer deleteByVideoId(@Param("videoId") String videoId);


	/**
	 * 根据VideoId获取对象
	 */
	 T selectByVideoId(@Param("videoId") String videoId);

	 /**
	  * @description: 更新各种数量
	  * @param videoId 视频id
	  * @param field 要更新的字段
	  * @param changeCount 改变了多少
	  * @return
	  * @author liuhd
	  * 2024/12/12 19:09
	  */

	 void updateCountInfo(@Param("videoId") String videoId,@Param("field") String field,@Param("changeCount") Integer changeCount);


	 CountInfoDto selectSumCountInfo(@Param("userId") String userId);
}
