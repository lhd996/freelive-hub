/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50739
 Source Host           : localhost:3306
 Source Schema         : easylive

 Target Server Type    : MySQL
 Target Server Version : 50739
 File Encoding         : 65001

 Date: 09/10/2024 19:07:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category_info
-- ----------------------------
DROP TABLE IF EXISTS `category_info`;
CREATE TABLE `category_info`  (
  `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增分类ID',
  `category_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类编码',
  `category_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `p_category_id` int(11) NOT NULL COMMENT '父级分类ID',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `background` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '背景图',
  `sort` tinyint(4) NOT NULL COMMENT '排序号',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `idx_key_category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for statistics_info
-- ----------------------------
DROP TABLE IF EXISTS `statistics_info`;
CREATE TABLE `statistics_info`  (
  `statistics_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计日期',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `data_type` tinyint(1) NOT NULL COMMENT '数据统计类型',
  `statistics_count` int(11) NULL DEFAULT NULL COMMENT '统计数量',
  PRIMARY KEY (`statistics_date`, `user_id`, `data_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据统计' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_action
-- ----------------------------
DROP TABLE IF EXISTS `user_action`;
CREATE TABLE `user_action`  (
  `action_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `video_user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频用户ID',
  `comment_id` int(11) NOT NULL DEFAULT 0 COMMENT '评论ID',
  `action_type` tinyint(1) NOT NULL COMMENT '0:评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币 ',
  `action_count` int(11) NOT NULL COMMENT '数量',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`action_id`) USING BTREE,
  UNIQUE INDEX `idx_key_video_comment_type_user`(`video_id`, `comment_id`, `action_type`, `user_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_type`(`action_type`) USING BTREE,
  INDEX `idx_action_time`(`action_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户行为 点赞、评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_focus
-- ----------------------------
DROP TABLE IF EXISTS `user_focus`;
CREATE TABLE `user_focus`  (
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `focus_user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `focus_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `focus_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '0:女 1:男 2:未知',
  `birthday` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出生日期',
  `school` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学校',
  `person_introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `join_time` datetime NOT NULL COMMENT '加入时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '0:禁用 1:正常',
  `notice_info` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间公告',
  `total_coin_count` int(11) NOT NULL COMMENT '硬币总数量',
  `current_coin_count` int(11) NOT NULL COMMENT '当前硬币数',
  `theme` tinyint(1) NOT NULL DEFAULT 1 COMMENT '主题',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_key_email`(`email`) USING BTREE,
  UNIQUE INDEX `idx_nick_name`(`nick_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `message_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID自增',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主体ID',
  `message_type` tinyint(1) NULL DEFAULT NULL COMMENT '消息类型',
  `send_user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送人ID',
  `read_type` tinyint(1) NULL DEFAULT NULL COMMENT '0:未读 1:已读',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `extend_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '扩展信息',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_read_type`(`read_type`) USING BTREE,
  INDEX `idx_message_type`(`message_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_video_series
-- ----------------------------
DROP TABLE IF EXISTS `user_video_series`;
CREATE TABLE `user_video_series`  (
  `series_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '列表ID',
  `series_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '列表名称',
  `series_description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `sort` tinyint(4) NOT NULL COMMENT '排序',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`series_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户视频序列归档' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_video_series_video
-- ----------------------------
DROP TABLE IF EXISTS `user_video_series_video`;
CREATE TABLE `user_video_series_video`  (
  `series_id` int(11) NOT NULL COMMENT '列表ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `sort` tinyint(4) NOT NULL COMMENT '排序',
  PRIMARY KEY (`series_id`, `video_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_comment
-- ----------------------------
DROP TABLE IF EXISTS `video_comment`;
CREATE TABLE `video_comment`  (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `p_comment_id` int(11) NOT NULL COMMENT '父级评论ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `video_user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回复内容',
  `img_path` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `reply_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回复人ID',
  `top_type` tinyint(4) NULL DEFAULT 0 COMMENT '0:未置顶  1:置顶',
  `post_time` datetime NOT NULL COMMENT '发布时间',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '喜欢数量',
  `hate_count` int(11) NULL DEFAULT 0 COMMENT '讨厌数量',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_post_time`(`post_time`) USING BTREE,
  INDEX `idx_top`(`top_type`) USING BTREE,
  INDEX `idx_p_id`(`p_comment_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_danmu
-- ----------------------------
DROP TABLE IF EXISTS `video_danmu`;
CREATE TABLE `video_danmu`  (
  `danmu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `post_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `text` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `mode` tinyint(1) NULL DEFAULT NULL COMMENT '展示位置',
  `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `time` int(11) NULL DEFAULT NULL COMMENT '展示时间',
  PRIMARY KEY (`danmu_id`) USING BTREE,
  INDEX `idx_file_id`(`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频弹幕' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_info
-- ----------------------------
DROP TABLE IF EXISTS `video_info`;
CREATE TABLE `video_info`  (
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `video_cover` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频封面',
  `video_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频名称',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '最后更新时间',
  `p_category_id` int(11) NOT NULL COMMENT '父级分类ID',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '分类ID',
  `post_type` tinyint(4) NOT NULL COMMENT '0:自制作  1:转载',
  `origin_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原资源说明',
  `tags` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `introduction` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `interaction` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '互动设置',
  `duration` int(11) NULL DEFAULT 0 COMMENT '持续时间（秒）',
  `play_count` int(11) NULL DEFAULT 0 COMMENT '播放数量',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数量',
  `danmu_count` int(11) NULL DEFAULT 0 COMMENT '弹幕数量',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数量',
  `coin_count` int(11) NULL DEFAULT 0 COMMENT '投币数量',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数量',
  `recommend_type` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐0:未推荐  1:已推荐',
  `last_play_time` datetime NULL DEFAULT NULL COMMENT '最后播放时间',
  PRIMARY KEY (`video_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_pcategory_id`(`p_category_id`) USING BTREE,
  INDEX `idx_recommend_type`(`recommend_type`) USING BTREE,
  INDEX `idx_last_update_time`(`last_play_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_info_file
-- ----------------------------
DROP TABLE IF EXISTS `video_info_file`;
CREATE TABLE `video_info_file`  (
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_index` int(11) NOT NULL COMMENT '文件索引',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `duration` int(11) NULL DEFAULT NULL COMMENT '持续时间（秒）',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频文件信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_info_file_post
-- ----------------------------
DROP TABLE IF EXISTS `video_info_file_post`;
CREATE TABLE `video_info_file_post`  (
  `file_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `upload_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '上传ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `file_index` int(11) NOT NULL COMMENT '文件索引',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `update_type` tinyint(4) NULL DEFAULT NULL COMMENT '0:无更新 1:有更新',
  `transfer_result` tinyint(4) NULL DEFAULT NULL COMMENT '0:转码中 1:转码成功 2:转码失败',
  `duration` int(11) NULL DEFAULT NULL COMMENT '持续时间（秒）',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `idx_key_upload_id`(`upload_id`, `user_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频文件信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_info_post
-- ----------------------------
DROP TABLE IF EXISTS `video_info_post`;
CREATE TABLE `video_info_post`  (
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '视频ID',
  `video_cover` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频封面',
  `video_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频名称',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '最后更新时间',
  `p_category_id` int(11) NOT NULL COMMENT '父级分类ID',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '分类ID',
  `status` tinyint(1) NOT NULL COMMENT '0:转码中 1转码失败 2:待审核 3:审核成功 4:审核失败',
  `post_type` tinyint(4) NOT NULL COMMENT '0:自制作  1:转载',
  `origin_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原资源说明',
  `tags` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `introduction` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `interaction` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '互动设置',
  `duration` int(11) NULL DEFAULT NULL COMMENT '持续时间（秒）',
  PRIMARY KEY (`video_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_pcategory_id`(`p_category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_play_history
-- ----------------------------
DROP TABLE IF EXISTS `video_play_history`;
CREATE TABLE `video_play_history`  (
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `video_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频ID',
  `file_index` int(11) NOT NULL COMMENT '文件索引',
  `last_update_time` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`(4), `video_id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频播放历史' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
