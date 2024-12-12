package com.lhd.entity.vo;

import com.lhd.entity.po.VideoInfo;

import java.util.List;

/**
 * @Author: liuhd
 * @Date: 2024/12/9 23:26
 * @Description:
 */
public class VideoInfoVO {
    private VideoInfo videoInfo;
    private List userActionList;



    public VideoInfoVO() {
    }



    public VideoInfoVO(VideoInfo videoInfo,List userActionList) {
        this.videoInfo = videoInfo;
        this.userActionList = userActionList;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }
    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
    public List getUserActionList() {
        return userActionList;
    }

    public void setUserActionList(List userActionList) {
        this.userActionList = userActionList;
    }
}
