package com.lhd.admin.controller;

import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.VideoStatusEnum;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: liuhd
 * @Date: 2024/12/9 16:05
 * @Description:
 */
public class VideoInfoController extends ABaseController {
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;
    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoPost(VideoInfoPostQuery videoInfoPostQuery){
        return getSuccessResponseVO(null);
    }
}
