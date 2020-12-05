/*
 Navicat Premium Data Transfer

 Source Server         : 虚拟机
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : 192.168.75.136:3306
 Source Schema         : seckill

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 04/12/2020 16:41:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `price` double(10, 0) NOT NULL DEFAULT 0,
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `sales` int(11) NOT NULL DEFAULT 0,
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES (6, 'iphone99', 800, '最好用的苹果手机', 139, 'https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3974550569,4161544558&fm=27&gp=0.jpg');
INSERT INTO `item` VALUES (7, 'iphone8', 600, '第二好用的苹果手机', 88, 'http://img5.imgtn.bdimg.com/it/u=2067197169,357050228&fm=26&gp=0.jpg');

-- ----------------------------
-- Table structure for item_stock
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL DEFAULT 0,
  `item_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `item_id_index`(`item_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
INSERT INTO `item_stock` VALUES (6, 73, 6);
INSERT INTO `item_stock` VALUES (7, 200, 7);

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `item_id` int(11) NOT NULL DEFAULT 0,
  `item_price` double NOT NULL DEFAULT 0,
  `amount` int(11) NOT NULL DEFAULT 0,
  `order_price` double NOT NULL DEFAULT 0,
  `promo_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2018111800000000', 9, 6, 0, 1, 0, 0);
INSERT INTO `order_info` VALUES ('2018111800000100', 9, 6, 800, 1, 800, 0);
INSERT INTO `order_info` VALUES ('2018111800000200', 9, 6, 800, 1, 800, 0);
INSERT INTO `order_info` VALUES ('2018111900000300', 9, 6, 800, 1, 800, 0);
INSERT INTO `order_info` VALUES ('2018111900000400', 9, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2018122200000500', 21, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019012300000600', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019012300000700', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019021000000800', 15, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019021000000900', 15, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022200001000', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022200001100', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022200001200', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022300001300', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001400', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001500', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001600', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001700', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001800', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400001900', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002000', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002100', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002200', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002300', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002400', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002500', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002600', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002700', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002800', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019022400002900', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030200003000', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030200003100', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030200003200', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030200003300', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030200003400', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030300003500', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030300003600', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030900003700', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030900003800', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030900003900', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030900004000', 22, 6, 100, 1, 100, 1);
INSERT INTO `order_info` VALUES ('2019030900004100', 22, 6, 100, 1, 100, 1);

-- ----------------------------
-- Table structure for promo
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `start_date` datetime(0) NOT NULL,
  `item_id` int(11) NOT NULL DEFAULT 0,
  `promo_item_price` double NOT NULL DEFAULT 0,
  `end_date` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promo
-- ----------------------------
INSERT INTO `promo` VALUES (1, 'iphone4抢购活动', '2018-01-19 00:04:30', 6, 100, '2019-12-31 00:00:00');

-- ----------------------------
-- Table structure for sequence_info
-- ----------------------------
DROP TABLE IF EXISTS `sequence_info`;
CREATE TABLE `sequence_info`  (
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `current_value` int(11) NOT NULL DEFAULT 0,
  `step` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sequence_info
-- ----------------------------
INSERT INTO `sequence_info` VALUES ('order_info', 42, 1);

-- ----------------------------
-- Table structure for stock_log
-- ----------------------------
DROP TABLE IF EXISTS `stock_log`;
CREATE TABLE `stock_log`  (
  `stock_log_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `item_id` int(11) NOT NULL DEFAULT 0,
  `amount` int(11) NOT NULL DEFAULT 0,
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '//1表示初始状态，2表示下单扣减库存成功，3表示下单回滚',
  PRIMARY KEY (`stock_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_log
-- ----------------------------
INSERT INTO `stock_log` VALUES ('05bcc80c65c74dc9969762fe63e58248', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('1151e433d0b84e6d93a0e20091071ebf', 6, 1, 1);
INSERT INTO `stock_log` VALUES ('1392bff227564b439903a70521429bee', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('3feff068ed8e4b91a757cce927bf0915', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('51ac037d3cbd4177a93e728d633d87e5', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('6a92c50c0c644475b03b476ecf16deae', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('6b5d7a909c1846aa879ab7e13acbc9ea', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('6b792aee5f574a8b9ad011e8e3962d04', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('777621f3639f45a08f0c7cc75863c2f4', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('7e84ccb5a2024f7f8a469aba4f5930ac', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('8428d20ff6ab480291d63c457cd17afa', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('912bf7696f6c4814bcd23a7409bff2a5', 6, 1, 2);
INSERT INTO `stock_log` VALUES ('df8cbfc3b153422397f12317cbe3c810', 6, 1, 2);

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT 0 COMMENT '//1代表男性，2代表女性',
  `age` int(11) NOT NULL DEFAULT 0,
  `telphone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `register_mode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '//byphone,bywechat,byalipay',
  `third_party_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `telphone_unique_index`(`telphone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, '第一个用户', 1, 30, '13521234859', 'byphone', '');
INSERT INTO `user_info` VALUES (15, 'teambition', 1, 20, '1312345678', 'byphone', '');
INSERT INTO `user_info` VALUES (20, '82030', 1, 1, '11111122', 'byphone', '');
INSERT INTO `user_info` VALUES (21, 'hzl', 1, 31, '13671573214', 'byphone', '');
INSERT INTO `user_info` VALUES (22, 'testuser', 1, 20, '13562514273', 'byphone', '');

-- ----------------------------
-- Table structure for user_password
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `user_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_password
-- ----------------------------
INSERT INTO `user_password` VALUES (1, 'ddlsjfjfjfjlf', 1);
INSERT INTO `user_password` VALUES (9, '4QrcOUm6Wau+VuBX8g+IPg==', 15);
INSERT INTO `user_password` VALUES (11, 'xMpCOKC5I4INzFCab3WEmw==', 20);
INSERT INTO `user_password` VALUES (12, '4QrcOUm6Wau+VuBX8g+IPg==', 21);
INSERT INTO `user_password` VALUES (13, '4QrcOUm6Wau+VuBX8g+IPg==', 22);

SET FOREIGN_KEY_CHECKS = 1;
