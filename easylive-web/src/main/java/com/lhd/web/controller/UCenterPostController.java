package com.lhd.web.controller;

import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2024/12/8 16:02
 * @Description: 视频发布
 */
@RestController
@RequestMapping("/ucenter")
@Validated
@Slf4j
public class UCenterPostController extends ABaseController{
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;
    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/postVide")
    public ResponseVO postVide((String videoId, @NotEmpty String videoCover, @NotEmpty @Size(max = 100) String videoName, @NotNull Integer pCategoryId,
                               Integer categoryId, @NotNull Integer postType, @NotEmpty @Size(max = 300) String tags, @Size(max = 2000) String introduction,
                               @Size(max = 3) String interaction, @NotEmpty String uploadFileList){

        return getSuccessResponseVO(null);
    }
}
