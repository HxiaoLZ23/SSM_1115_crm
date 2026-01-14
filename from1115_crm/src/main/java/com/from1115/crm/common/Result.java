package com.from1115.crm.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    /*x响应码 200表示成功 -1表示失败 */
    private Integer code = 200;

  /*响应消息，只有响应码为-1时才给该属性赋值*/
    private  String msg;

    /*返回的数据，只有查询时才给该属性赋值*/
    private Object data;

    private long total = 0L;

    public Result(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
