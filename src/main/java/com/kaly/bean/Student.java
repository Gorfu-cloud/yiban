package com.kaly.bean;

import lombok.*;

/**
 * Created by 国瑚 on 2020/7/14.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Student {
    String yhm;//学号
    String mm;//密码int
    int year;//学年
    int term;//学期
}
