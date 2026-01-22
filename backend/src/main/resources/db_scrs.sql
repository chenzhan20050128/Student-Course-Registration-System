/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Host           : localhost:3306
Source Database       : scrs

Target Server Type    : MYSQL


User:
admin:admin123
Student:
Zhang San:password123
Li Si:securePass!45
Teacher:
Li Hua:pass1234
Liu Qin:pass5678
*/

-- source D:\technologySoftware\SCRS\scrs\db_scrs.sql

DROP DATABASE IF EXISTS scrs;
CREATE DATABASE scrs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE scrs;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `college`
-- ----------------------------
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `cname` VARCHAR(255) NOT NULL COMMENT 'College name',
  `descr` VARCHAR(2047) DEFAULT NULL COMMENT 'Description',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of `college`
-- ----------------------------
INSERT INTO `college` (`id`, `cname`, `descr`) VALUES
(1, 'Computer Science College', 'The discipline of Computer Science at Nanjing University originates from the long-standing Mathematics Department of Nanjing University'),
(2, 'Software College', 'The Software College of Nanjing University is a teaching and research-oriented engineering college'),
(3, 'Mathematics College', 'Focused on mathematics research'),
(4, 'Civil Engineering College', 'Cultivating talents in civil engineering, architectural engineering, transportation engineering, and fields related to infrastructure and sustainable development'),
(5, 'Economics and Management College', 'Offers courses in Economics, Business Administration, Financial Management with the aim of cultivating modern managerial talents and economists'),
(6, 'Medical College', 'Committed to medical education and research, offering comprehensive courses from basic to clinical medicine'),
(7, 'Law College', 'Offers courses on Civil Law, Criminal Law, International Law, and trains legal professionals'),
(8, 'Humanities College', 'Covers disciplines such as history, literature, philosophy, and art, focusing on humanistic literacy and social science research'),
(9, 'Foreign Languages College', 'Provides professional education in English, French, German, Japanese, etc., focusing on cross-cultural exchange and research'),
(10, 'Environmental Science College', 'Focuses on research and talent cultivation in the fields of environmental protection, resource management, and ecological governance'),
(11, 'Materials Science and Engineering College', 'Explores new phenomena and applications in the field of materials, cultivating innovative engineering talents'),
(12, 'Chemistry and Chemical Engineering College', 'Dedicated to foundational chemistry research and chemical engineering applications, covering materials chemistry, biochemistry, etc.');

-- ----------------------------
-- Table structure for `course`
-- ----------------------------
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

-- ----------------------------
-- Records of `course`
-- ----------------------------
INSERT INTO `course` (`course_id`, `course_name`, `college`, `instructor_name`, `campus`, `classroom`, `enrolled_count`, `capacity`, `credits`, `description`, `start_week`, `end_week`, `type`) VALUES
(1, 'Data Structure', 'Computer Science College', 'Li Hua', 'Main Campus', 'A101', 0, 50, 4, 'Course on data structures, explaining basic data organization and algorithms', 1, 16, 'Required'),
(2, 'Advanced Mathematics', 'Mathematics College', 'Xiao Yuanming', 'Main Campus', 'B202', 0, 60, 5, 'Basic mathematics course covering calculus and more', 1, 16, 'Required'),
(3, 'Software Engineering and Computing', 'Software College', 'Liu Qin', 'Main Campus', 'C301', 0, 40, 3, 'Basic theory of software engineering', 1, 16, 'Required'),
(4, 'Linear Algebra', 'Mathematics College', 'Nie Ziwei', 'Main Campus', 'A102', 0, 60, 3, 'Matrix theory and linear transformations', 1, 16, 'Required'),
(5, 'Operating System', 'Computer Science College', 'Zhang Wei', 'Main Campus', 'C302', 0, 30, 4, 'Principles and applications of operating systems', 1, 16, 'Required'),
(6, 'Demand and Business Model Analysis', 'Software College', 'Liu Tao', 'Main Campus', 'C303', 0, 45, 4, 'Requirements engineering', 1, 16, 'Elective');

-- ----------------------------
-- Table structure for `major`
-- ----------------------------

DROP TABLE IF EXISTS `major`;
CREATE TABLE `major` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `mname` VARCHAR(255) NOT NULL COMMENT 'Major name',
  `college` VARCHAR(255) NOT NULL COMMENT 'College',
  `descr` VARCHAR(2047) DEFAULT NULL COMMENT 'Description',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of `major`
-- ----------------------------
INSERT INTO `major` (`id`, `mname`, `college`, `descr`) VALUES
(1, 'Software Engineering', 'Software College', 'The Software Engineering major is one of the most important and popular disciplines in the modern information technology field. With the acceleration of digitalization and informatization, the application of software in various industries has become ubiquitous, and the demand for software engineers is also continuously growing. This major aims to cultivate professional software development and management talents with both solid theoretical foundations and practical abilities.'),
(2, 'Computer Science and Technology', 'Computer Science College', 'Cultivates design, analysis, and application capabilities of computer systems'),
(3, 'Civil Engineering', 'Civil Engineering College', 'Covers specialties in architecture, transportation, and bridge engineering, emphasizing structural design and engineering management'),
(4, 'Finance', 'Economics and Management College', 'Provides comprehensive knowledge of financial markets, banking, and investment'),
(5, 'Mathematics and Applied Mathematics', 'Mathematics College', 'Trains students in theoretical mathematics, applied mathematics, and related fields, covering branches such as analysis, algebra, and geometry.');

-- ----------------------------
-- Table structure for `student`
-- ----------------------------

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `sname` VARCHAR(255) NOT NULL COMMENT 'Student name',
  `password` VARCHAR(255) NOT NULL COMMENT 'Password',
  `sex` VARCHAR(100) DEFAULT NULL COMMENT 'Gender',
  `age` INT DEFAULT NULL COMMENT 'Age',
  `major` VARCHAR(255) DEFAULT NULL COMMENT 'Major',
  `college` VARCHAR(255) DEFAULT NULL COMMENT 'College',
  `simage` VARCHAR(255) DEFAULT NULL COMMENT 'Profile picture',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of `student`

-- ----------------------------
INSERT INTO `student` (`id`, `sname`, `password`, `sex`, `age`, `major`, `college`, `simage`) VALUES
(1, 'Zhang San', '482c811da5d5b4bc6d497ffa98491e38', 'Male', 20, 'Computer Science and Technology', 'Computer Science College', 'image1.jpg'),
(2, 'Li Si', '15901c034ced2564502166d642eb0c65', 'Female', 21, 'Computer Science and Technology', 'Computer Science College', 'image2.jpg'),
(3, 'Wang Wu', 'myPass456', 'Male', 22, 'Computer Science and Technology', 'Computer Science College', 'image3.jpg'),
(4, 'Zhao Liu', 'pass7890', 'Female', 23, 'Computer Science and Technology', 'Computer Science College', 'image4.jpg'),
(5, 'Sun Qi', 'admin123', 'Male', 24, 'Computer Science and Technology', 'Computer Science College', 'image5.jpg'),
(6, 'Zhou Ba', 'pass0987', 'Female', 20, 'Software Engineering', 'Software College', 'image6.jpg'),
(7, 'Wu Jiu', 'hunter123', 'Male', 21, 'Software Engineering', 'Software College', 'image7.jpg'),
(8, 'Zheng Shi', 'pass7654', 'Female', 22, 'Software Engineering', 'Software College', 'image8.jpg'),
(9, 'Zhu Shiyi', 'secure987', 'Male', 23, 'Software Engineering', 'Software College', 'image9.jpg'),
(10, 'He Shier', 'wordpass1', 'Female', 24, 'Software Engineering', 'Software College', 'image10.jpg');

-- ----------------------------
-- Table structure for `teacher`
-- ----------------------------

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tname` VARCHAR(255) NOT NULL COMMENT 'Teacher name',
  `password` VARCHAR(255) NOT NULL COMMENT 'Password',
  `email` VARCHAR(255) DEFAULT NULL COMMENT 'Email',
  `sex` VARCHAR(100) DEFAULT NULL COMMENT 'Gender',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone number',
  `age` INT DEFAULT NULL COMMENT 'Age',
  `major` VARCHAR(255) DEFAULT NULL COMMENT 'Major',
  `timage` VARCHAR(255) DEFAULT NULL COMMENT 'Profile picture',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of `teacher`
-- ----------------------------
INSERT INTO `teacher` (`id`, `tname`, `password`, `email`, `sex`, `phone`, `age`, `major`, `timage`) VALUES
(1, 'Li Hua', 'b4af804009cb036a4ccdc33431ef9ac9', 'wang@mail.com', 'Male', '13800138000', 35, 'Computer Science and Technology', 'teacher1.jpg'),
(2, 'Liu Qin', 'ee8ef2f5441316e0305ae007372de337', 'zhao@mail.com', 'Male', '13900139000', 40, 'Software Engineering', 'teacher2.jpg'),
(3, 'Zhang Wei', 'pass1111', 'sun@mail.com', 'Male', '13700137000', 45, 'Computer Science and Technology', 'teacher3.jpg'),
(4, 'Liu Tao', 'pass2222', 'li@mail.com', 'Female', '13600136000', 38, 'Software Engineering', 'teacher4.jpg'),
(5, 'Xiao Yuanming', 'pass3333', 'chen@mail.com', 'Male', '13500135000', 42, 'Mathematics and Applied Mathematics', 'teacher5.jpg'),
(6, 'Nie Ziwei', 'pass3333', 'nie@mail.com', 'Male', '13500138123', 42, 'Mathematics and Applied Mathematics', 'teacher6.jpg');

-- ----------------------------
-- Table structure for `teacher_course`
-- ----------------------------
DROP TABLE IF EXISTS `teacher_course`;
CREATE TABLE `teacher_course` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tid` INT NOT NULL COMMENT 'Teacher ID',
  `cid` INT NOT NULL COMMENT 'Course ID',
  PRIMARY KEY (`id`),
  KEY `tid` (`tid`),
  KEY `cid` (`cid`),
  CONSTRAINT `teacher_course_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_course_ibfk_2` FOREIGN KEY (`cid`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert records using the aforementioned mappings between courses and teachers
INSERT INTO `teacher_course` (`id`, `tid`, `cid`) VALUES
(1, 1, 1),  -- Li Hua -> Data Structure
(2, 5, 2),  -- Xiao Yuanming -> Advanced Mathematics
(3, 2, 3),  -- Liu Qin -> Software Engineering and Computing
(4, 6, 4),  -- Nie Ziwei -> Linear Algebra
(5, 3, 5),  -- Zhang Wei -> Operating System
(6, 4, 6);  -- Liu Tao -> Demand and Business Model Analysis
-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `email` VARCHAR(255) COMMENT 'Email address',
  `password` VARCHAR(255) NOT NULL COMMENT 'Password',
  `username` VARCHAR(255) NOT NULL COMMENT 'Username',
  `phone` VARCHAR(20) COMMENT 'Phone number',
  `image` VARCHAR(255) COMMENT 'Profile image path',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of `user`
-- ----------------------------
INSERT INTO `user` (`id`, `email`, `password`, `username`, `phone`, `image`) VALUES
(1, 'admin@example.com', '0192023a7bbd73250516f069df18b500', 'admin', '13800000000', '/images/default.jpg'),
(2, 'student1@example.com', '482c811da5d5b4bc6d497ffa98491e38', 'Zhang San', '13811111111', 'image1.jpg.jpg'),
(3, 'student2@example.com', '15901c034ced2564502166d642eb0c65', 'Li Si', '13822222222', 'image2.jpg'),
(4, 'wang@mail.com', 'b4af804009cb036a4ccdc33431ef9ac9', 'Li Hua', '13800138000', 'teacher1.jpg'),
(5, 'zhao@mail.com', 'ee8ef2f5441316e0305ae007372de337', 'Liu Qin', '13900139000', 'teacher2.jpg');

/*
Md5Test.java
String[] passwords = {"admin123", "password123", "securePass!45", "pass1234", "pass5678"};
       for (String password : passwords){
            String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
            System.out.println(passwordAfterMD5);
     }
    User:
admin:admin123
Student:
Zhang San:password123
Li Si:securePass!45
Teacher:
Li Hua:pass1234
Liu Qin:pass5678


*/


CREATE TABLE `student_course` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `sid` INT NOT NULL,
  `cid` INT NOT NULL,
  `status` INT DEFAULT 1 COMMENT '1=enrolled, 0=withdrawn',
  FOREIGN KEY (`sid`) REFERENCES `student`(`id`),
  FOREIGN KEY (`cid`) REFERENCES `course`(`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `student_course` (`sid`, `cid`, `status`) VALUES
(1, 1, 1);


SET FOREIGN_KEY_CHECKS=1;