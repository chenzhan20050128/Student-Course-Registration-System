-- ============================================
-- 数据库字段映射修复脚本
-- 用途: 将course表字段从旧命名更新为新命名
-- 执行时间: 2026-01-22
-- ============================================

USE scrs;

-- 步骤1: 禁用外键检查
SET FOREIGN_KEY_CHECKS=0;

-- 步骤2: 备份现有course表数据（可选，建议执行）
DROP TABLE IF EXISTS `course_backup`;
CREATE TABLE `course_backup` AS SELECT * FROM `course`;

-- 步骤3: 删除依赖course表的外键约束
ALTER TABLE `teacher_course` DROP FOREIGN KEY IF EXISTS `teacher_course_ibfk_2`;
ALTER TABLE `student_course` DROP FOREIGN KEY IF EXISTS `student_course_fk_cid`;

-- 步骤4: 重建course表结构
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `course_id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key (Course ID)',
  `course_name` VARCHAR(255) NOT NULL COMMENT 'Course name',
  `college` VARCHAR(255) NOT NULL COMMENT 'College/Department',
  `instructor_name` VARCHAR(255) NOT NULL COMMENT 'Instructor name',
  `campus` VARCHAR(255) NOT NULL COMMENT 'Campus location',
  `classroom` VARCHAR(255) DEFAULT NULL COMMENT 'Classroom number',
  `enrolled_count` INT NOT NULL DEFAULT 0 COMMENT 'Current enrolled count',
  `capacity` INT NOT NULL COMMENT 'Maximum capacity',
  `credits` INT DEFAULT NULL COMMENT 'Course credits',
  `description` VARCHAR(1023) DEFAULT NULL COMMENT 'Course description',
  `start_week` INT DEFAULT NULL COMMENT 'Start week',
  `end_week` INT DEFAULT NULL COMMENT 'End week',
  `type` VARCHAR(50) DEFAULT NULL COMMENT 'Course type (required/elective)',
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 步骤5: 插入示例课程数据
INSERT INTO `course` (`course_id`, `course_name`, `college`, `instructor_name`, `campus`, `classroom`, `enrolled_count`, `capacity`, `credits`, `description`, `start_week`, `end_week`, `type`) VALUES
(1, 'Data Structure', 'Computer Science College', 'Li Hua', 'Main Campus', 'A101', 0, 50, 4, 'Course on data structures, explaining basic data organization and algorithms', 1, 16, 'Required'),
(2, 'Advanced Mathematics', 'Mathematics College', 'Xiao Yuanming', 'Main Campus', 'B202', 0, 60, 5, 'Basic mathematics course covering calculus and more', 1, 16, 'Required'),
(3, 'Software Engineering and Computing', 'Software College', 'Liu Qin', 'Main Campus', 'C301', 0, 40, 3, 'Basic theory of software engineering', 1, 16, 'Required'),
(4, 'Linear Algebra', 'Mathematics College', 'Nie Ziwei', 'Main Campus', 'A102', 0, 60, 3, 'Matrix theory and linear transformations', 1, 16, 'Required'),
(5, 'Operating System', 'Computer Science College', 'Zhang Wei', 'Main Campus', 'C302', 0, 30, 4, 'Principles and applications of operating systems', 1, 16, 'Required'),
(6, 'Demand and Business Model Analysis', 'Software College', 'Liu Tao', 'Main Campus', 'C303', 0, 45, 4, 'Requirements engineering', 1, 16, 'Elective');

-- 步骤6: 重建外键约束
ALTER TABLE `teacher_course` 
  ADD CONSTRAINT `teacher_course_ibfk_2` 
  FOREIGN KEY (`cid`) REFERENCES `course` (`course_id`) 
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `student_course` 
  ADD CONSTRAINT `student_course_fk_cid` 
  FOREIGN KEY (`cid`) REFERENCES `course` (`course_id`) 
  ON DELETE CASCADE ON UPDATE CASCADE;

-- 步骤7: 清空并重置选课关系数据（如果需要保留，请注释此部分）
TRUNCATE TABLE `student_course`;
-- 插入测试数据
INSERT INTO `student_course` (`sid`, `cid`, `status`) VALUES (1, 1, 1);

-- 步骤8: 启用外键检查
SET FOREIGN_KEY_CHECKS=1;

-- 步骤9: 验证表结构
SHOW CREATE TABLE `course`;
SELECT * FROM `course`;

-- 完成提示
SELECT 'Database field mapping update completed successfully!' AS Status;
