package com.scrs;/*
 * @date 12/07 13:50
 */

import org.springframework.util.DigestUtils;

/*
INSERT INTO `user` (`id`, `email`, `password`, `username`, `phone`, `image`) VALUES
(1, 'admin@example.com', 'admin123', 'admin', '13800000000', '/images/default.jpg'),
(2, 'student1@example.com', 'password123', 'Zhang San', '13811111111', 'image1.jpg.jpg'),
(3, 'student2@example.com', 'securePass!45', 'Li Si', '13822222222', 'image2.jpg'),
(4, 'wang@mail.com', 'pass1234', 'Li Hua', '13800138000', 'teacher1.jpg'),
(5, 'zhao@mail.com', 'pass5678', 'Liu Qin', '13900139000', 'teacher2.jpg');

 */
public class MD5Test {
    public static void main(String[] args) {
        System.out.println("MD5Test.main");
        String[] passwords = {"admin123", "password123", "securePass!45", "pass1234", "pass5678"};
        for (String password : passwords){
            String passwordAfterMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
            System.out.println(passwordAfterMD5);
        }
        return ;
    }
}
