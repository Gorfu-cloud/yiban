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
public class GradeDetail {
    String cjfx;//成绩分项
    String ratio;//分项比例
    String grade;//分数
}
