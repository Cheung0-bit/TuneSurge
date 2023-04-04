/*
 Navicat Premium Data Transfer

 Source Server         : debian
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : zhang0.cool:3306
 Source Schema         : tunesurge_media

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 04/04/2023 15:10:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for media_files
-- ----------------------------
DROP TABLE IF EXISTS `media_files`;
CREATE TABLE `media_files`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件ID，MD5值',
  `user_id` bigint NULL DEFAULT NULL COMMENT '所属用户ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
  `file_type` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型（图片，文档，视频 ）',
  `tags` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储目录',
  `file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储路径',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '媒资文件访问路径',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '状态 1：正常 2：不展示',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `audit_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '审核状态',
  `audit_mind` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of media_files
-- ----------------------------
INSERT INTO `media_files` VALUES ('76b5f0420e6abefb2393d9c4b2bfa0d5', 1, '对不起.avi', 'video', 'MV作品', 'video', '7/6/76b5f0420e6abefb2393d9c4b2bfa0d5/76b5f0420e6abefb2393d9c4b2bfa0d5.avi', '/video/7/6/76b5f0420e6abefb2393d9c4b2bfa0d5/76b5f0420e6abefb2393d9c4b2bfa0d5.mp4', '2023-03-27 13:27:24', '2023-03-27 13:27:24', '1', NULL, '0', NULL, 11264280);
INSERT INTO `media_files` VALUES ('a5eff21fcfe3483433dbaf9aad567211', 1, '对不起.png', 'image', NULL, 'mediafiles', '2023/03/27/a5eff21fcfe3483433dbaf9aad567211.png', '/mediafiles/2023/03/27/a5eff21fcfe3483433dbaf9aad567211.png', '2023-03-27 13:14:51', '2023-03-27 13:14:51', '1', NULL, '0', NULL, 238927);

-- ----------------------------
-- Table structure for media_process
-- ----------------------------
DROP TABLE IF EXISTS `media_process`;
CREATE TABLE `media_process`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件标识',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
  `bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储桶',
  `file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储路径',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态 1：未处理 2：处理成功 3：处理失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '媒资文件访问地址',
  `error_msg` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of media_process
-- ----------------------------

-- ----------------------------
-- Table structure for media_process_history
-- ----------------------------
DROP TABLE IF EXISTS `media_process_history`;
CREATE TABLE `media_process_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件标识',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
  `bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储桶',
  `file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储路径',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态 1：未处理 2：处理成功 3：处理失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '媒资文件访问地址',
  `error_msg` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of media_process_history
-- ----------------------------
INSERT INTO `media_process_history` VALUES (7, '76b5f0420e6abefb2393d9c4b2bfa0d5', '对不起.avi', 'video', '7/6/76b5f0420e6abefb2393d9c4b2bfa0d5/76b5f0420e6abefb2393d9c4b2bfa0d5.avi', '2', '2023-03-27 13:27:24', '2023-03-27 21:32:14', '/video/7/6/76b5f0420e6abefb2393d9c4b2bfa0d5/76b5f0420e6abefb2393d9c4b2bfa0d5.mp4', NULL);

SET FOREIGN_KEY_CHECKS = 1;
