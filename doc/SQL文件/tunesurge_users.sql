/*
 Navicat Premium Data Transfer

 Source Server         : debian
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : zhang0.cool:3306
 Source Schema         : tunesurge_users

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 04/04/2023 15:10:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ts_menu
-- ----------------------------
DROP TABLE IF EXISTS `ts_menu`;
CREATE TABLE `ts_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单编码',
  `p_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父菜单ID',
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求地址',
  `is_menu` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否是菜单',
  `level` int NULL DEFAULT NULL COMMENT '菜单层级',
  `sort` int NULL DEFAULT NULL COMMENT '菜单排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单状态',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ts_menu
-- ----------------------------
INSERT INTO `ts_menu` VALUES (1, 'root', '0', '系统根目录', NULL, '0', 0, 1, '1', NULL, '2023-03-17 03:42:56', '2023-03-17 03:42:56');
INSERT INTO `ts_menu` VALUES (2, 'ts_sys', '1', '系统管理', NULL, '1', 1, 10, '1', NULL, '2023-03-17 03:45:09', '2023-03-17 03:45:09');
INSERT INTO `ts_menu` VALUES (3, 'ts_sys_user', NULL, '用户管理', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:15:25', '2023-03-17 04:15:25');
INSERT INTO `ts_menu` VALUES (4, 'ts_sys_user_add', NULL, '添加用户', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:16:01', '2023-03-17 04:16:01');
INSERT INTO `ts_menu` VALUES (5, 'ts_sys_user_edit', NULL, '修改用户', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:16:26', '2023-03-17 04:16:26');
INSERT INTO `ts_menu` VALUES (6, 'ts_sys_user_view', NULL, '用户列表', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:16:50', '2023-03-17 04:16:50');
INSERT INTO `ts_menu` VALUES (7, 'ts_sys_user_delete', NULL, '删除用户', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (8, 'ts_sys_role', NULL, '角色管理', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (9, 'ts_sys_role_add', NULL, '添加角色', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (10, 'ts_sys_role_edit', NULL, '编辑角色', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (11, 'ts_sys_role_delete', NULL, '删除角色', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (12, 'ts_sys_role_view', NULL, '角色列表', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:01');
INSERT INTO `ts_menu` VALUES (13, 'ts_sys_role_permission', NULL, '角色配权', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-17 04:20:01', '2023-03-17 04:20:36');
INSERT INTO `ts_menu` VALUES (14, 'ts_sys_mv_audit', NULL, 'MV作品审核', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-28 03:45:46', '2023-03-28 03:45:46');

-- ----------------------------
-- Table structure for ts_permission
-- ----------------------------
DROP TABLE IF EXISTS `ts_permission`;
CREATE TABLE `ts_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `menu_id` int NOT NULL COMMENT '菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ts_permission
-- ----------------------------
INSERT INTO `ts_permission` VALUES (1, 6, 1, '2023-03-17 04:28:39', '2023-03-17 04:28:39');
INSERT INTO `ts_permission` VALUES (2, 6, 2, '2023-03-17 04:29:20', '2023-03-17 04:29:20');
INSERT INTO `ts_permission` VALUES (3, 6, 14, '2023-03-28 03:46:54', '2023-03-28 03:46:54');

-- ----------------------------
-- Table structure for ts_role
-- ----------------------------
DROP TABLE IF EXISTS `ts_role`;
CREATE TABLE `ts_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色代码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ts_role
-- ----------------------------
INSERT INTO `ts_role` VALUES (1, '用户', 'user', '普通用户', '2023-03-17 11:32:45', '2023-03-17 03:33:01', '1');
INSERT INTO `ts_role` VALUES (2, '音乐人', 'musician', '普通用户发表原创作品，并通过审核即可成为音乐人', '2023-03-17 03:34:12', '2023-03-17 03:34:12', '1');
INSERT INTO `ts_role` VALUES (3, '作品审核', 'work_auditor', '音乐作品审核发布人', '2023-03-17 03:36:49', '2023-03-17 03:36:49', '1');
INSERT INTO `ts_role` VALUES (4, '评论审核', 'remark_auditor', '用户评论审核人', '2023-03-17 03:37:45', '2023-03-17 03:37:45', '1');
INSERT INTO `ts_role` VALUES (5, '系统运维', 'system_maintenance', '系统设备运维人员', '2023-03-17 03:38:44', '2023-03-17 03:38:44', '1');
INSERT INTO `ts_role` VALUES (6, '超级管理员', 'super_admin', '系统超级管理员', '2023-03-17 03:39:10', '2023-03-17 03:39:10', '1');

-- ----------------------------
-- Table structure for ts_user
-- ----------------------------
DROP TABLE IF EXISTS `ts_user`;
CREATE TABLE `ts_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID',
  `username` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户系统名称',
  `password` varchar(96) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户密码（加密存储）',
  `salt` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加密盐',
  `wx_unionid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户背景图',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户性别',
  `email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `cell_phone` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户手机号',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ts_user
-- ----------------------------
INSERT INTO `ts_user` VALUES (1, 'zhanglin', '$2a$10$yQJVvBqpOkUXNHCFo5BSauObbW4FRZYXLF41vhNvNMyy25KhrHgPO', NULL, NULL, '无情的帅哥', 'xx', 'xx', '1', 'xx', '11', '正常', '2023-03-17 03:28:38', '2023-03-19 04:30:40');
INSERT INTO `ts_user` VALUES (2, 'Cheng Zitao', 'ZiYqtUlttm', NULL, NULL, 'Cheng Zitao', 'RtOpxzRFzy', 'z0VlNODkkK', '1', 'cheung0-bit@qq.com', '330-105-7858', '正常', '2009-05-14 14:12:51', '2023-04-03 15:07:48');
INSERT INTO `ts_user` VALUES (3, 'Ichikawa Ryota', '5yLp3eC089', NULL, NULL, 'Ichikawa Ryota', 'UhlxTMvULO', 'JXTJ5sS0hk', '1', 'ichikawaryo@icloud.com', '212-054-0980', '正常', '2015-07-12 11:10:47', '2005-12-26 03:56:44');
INSERT INTO `ts_user` VALUES (4, 'Stephen Crawford', 'nTLbNllIRV', NULL, NULL, 'Stephen Crawford', '003TCFhbAF', 'yrP2H0H55D', '1', 'stephen215@mail.com', '213-607-9447', '正常', '2010-05-09 11:32:44', '2012-05-19 14:32:35');
INSERT INTO `ts_user` VALUES (5, 'Tong Yun Fat', 'joICSJq04O', NULL, NULL, 'Tong Yun Fat', 'DiZsHJw80l', 'Dlkr4qOZMR', '1', 'tyunfat76@gmail.com', '330-442-5540', '正常', '2004-10-09 18:57:36', '2004-10-09 01:11:16');
INSERT INTO `ts_user` VALUES (6, 'Li Yunxi', '14Bc8ubiMa', NULL, NULL, 'Li Yunxi', '5X9FVoIYu9', '11NzLBCqSi', '1', 'yunli@gmail.com', '614-715-6128', '正常', '2020-02-08 20:42:45', '2003-02-03 01:26:39');
INSERT INTO `ts_user` VALUES (7, 'Katherine Phillips', 'tzmmPyYlyM', NULL, NULL, 'Katherine Phillips', 'Nw9oEOTivR', 'JqFDD2kwyY', '1', 'phillkat@outlook.com', '80-1900-3388', '正常', '2008-03-20 03:16:57', '2016-10-13 21:53:19');
INSERT INTO `ts_user` VALUES (8, 'Arai Ikki', 'BFrWEOd1Fu', NULL, NULL, 'Arai Ikki', 'yymoDpS4rj', 'vmqNpM40sr', '1', 'ikkiarai@outlook.com', '135-7276-2725', '正常', '2003-09-13 08:19:30', '2016-07-30 02:16:29');
INSERT INTO `ts_user` VALUES (9, 'Takagi Mio', 'MRJp0MDvLl', NULL, NULL, 'Takagi Mio', 'dvQFo1CyIn', 'dnXvY8cEg5', '1', 'miotak8@gmail.com', '769-9680-0413', '正常', '2022-05-13 17:04:02', '2011-11-26 14:34:58');
INSERT INTO `ts_user` VALUES (10, 'Yao Zhiyuan', '9Z0cNVpQSj', NULL, NULL, 'Yao Zhiyuan', 'lb4pVlpH66', 'ENNwoei0yV', '1', 'yz7@gmail.com', '90-7934-5529', '正常', '2004-05-31 06:39:01', '2016-07-11 05:19:45');
INSERT INTO `ts_user` VALUES (11, 'Julia Bell', '8lLdG5CZc1', NULL, NULL, 'Julia Bell', '7gpKCSn5pS', 'MvTMZZBcDy', '1', 'juliabe7@outlook.com', '66-833-2050', '正常', '2009-05-15 13:30:55', '2020-04-20 18:51:13');
INSERT INTO `ts_user` VALUES (12, 'Kono Yuto', 'xQiMLiIcMs', NULL, NULL, 'Kono Yuto', 'mXihWirwR1', 'lWY9nCa55U', '1', 'yutokono@yahoo.com', '7296 678863', '正常', '2004-06-04 00:41:41', '2001-05-23 21:23:04');
INSERT INTO `ts_user` VALUES (13, 'Long Lu', 'dToTDzwOWv', NULL, NULL, 'Long Lu', 'Aw1VjjLXr4', 'RXyQsVeffu', '1', 'longlu75@outlook.com', '174-1887-4680', '正常', '2016-05-19 08:51:05', '2021-04-23 14:10:35');
INSERT INTO `ts_user` VALUES (14, 'Sano Hikari', 'lu0b4J8FlS', NULL, NULL, 'Sano Hikari', 'p3o8hFv8N4', 'ca3aH611sX', '1', 'hs1107@hotmail.com', '74-178-3551', '正常', '2011-05-29 19:49:36', '2022-07-15 17:54:47');
INSERT INTO `ts_user` VALUES (15, 'Ishii Mio', 'GtVCmlGCPg', NULL, NULL, 'Ishii Mio', '7wBIyuUmtV', 'NYbaEHgoL5', '1', 'ishiimio@mail.com', '7624 919552', '正常', '2004-12-28 19:40:10', '2007-12-28 01:32:11');
INSERT INTO `ts_user` VALUES (16, 'Liao Wing Suen', 'FDseK8XROw', NULL, NULL, 'Liao Wing Suen', 'EYeA8qtScq', 'vjZEIoArHO', '1', 'liaowingsuen@gmail.com', '90-7412-9863', '正常', '2000-03-20 01:57:33', '2001-11-01 11:20:46');
INSERT INTO `ts_user` VALUES (17, 'Heung Fu Shing', 'c7yhjVwBnw', NULL, NULL, 'Heung Fu Shing', 'AlT1wvtrXu', 'CqVQFRhQlx', '1', 'fuheung@outlook.com', '(121) 435 9036', '正常', '2006-10-10 08:18:08', '2009-10-21 01:35:37');
INSERT INTO `ts_user` VALUES (18, 'Yuen Kwok Ming', 'p3IN3R0yEN', NULL, NULL, 'Yuen Kwok Ming', 'YwefrpzamY', 'sRsel5LaIE', '1', 'kwokmingyuen@icloud.com', '312-598-3003', '正常', '2006-05-09 14:50:30', '2003-11-06 17:07:02');
INSERT INTO `ts_user` VALUES (19, 'Sheh Kwok Yin', 'SHwMBWLSPr', NULL, NULL, 'Sheh Kwok Yin', 'LxnxfcAlTu', '5I1G8AmM3a', '1', 'shehkw03@gmail.com', '330-361-1926', '正常', '2004-09-24 16:39:40', '2009-04-28 10:09:24');
INSERT INTO `ts_user` VALUES (20, 'Ti Wing Suen', 'RGhty4xBJr', NULL, NULL, 'Ti Wing Suen', 'hKqWwVXBHu', 'vU21dxd3Xc', '1', 'tws@outlook.com', '755-7235-2671', '正常', '2008-12-31 17:17:02', '2008-12-20 21:10:48');

-- ----------------------------
-- Table structure for ts_user_role
-- ----------------------------
DROP TABLE IF EXISTS `ts_user_role`;
CREATE TABLE `ts_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色中间表ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ts_user_role
-- ----------------------------
INSERT INTO `ts_user_role` VALUES (1, 1, 6, '2023-03-17 03:40:09', '2023-03-17 03:40:09', NULL);

SET FOREIGN_KEY_CHECKS = 1;
