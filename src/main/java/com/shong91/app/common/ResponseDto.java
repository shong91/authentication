package com.shong91.app.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

  private LocalDateTime timestmap = LocalDateTime.now();

  private Integer code;

  private String codeName;

  private String desc;

  private T data;

  public ResponseDto(ResultCode resultCode) {
    init(resultCode);
  }

  public ResponseDto(ResultCode resultCode, T data) {
    init(resultCode);
    this.data = data;
  }

  private void init(ResultCode resultCode) {
    this.code = resultCode.getCode();
    this.codeName = resultCode.name();
    this.desc = resultCode.getDescription();
  }
}
