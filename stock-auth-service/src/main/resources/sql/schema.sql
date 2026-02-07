CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` int(11) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `vip` int(11) DEFAULT '0' COMMENT '是否为VIP（0-普通用户，1-VIP用户）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号：admin / admin123
-- 这个BCrypt密码是通过 Spring Security 的 BCryptPasswordEncoder.encode("admin123") 生成的
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `status`)
SELECT 'admin', '$2a$10$wRJ3T6X1vNQKz0mZ6LqP0.4Jq1PvXjLsrUdrOX3CgV.SV6wZ/Adj9S', '管理员', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `username` = 'admin');
