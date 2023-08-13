package com.shong91.app.common;

import com.shong91.app.util.TokenUser;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedDate
  @Column(insertable = false)
  private LocalDateTime updatedAt;

  @LastModifiedBy
  private String updatedBy;

  private LocalDateTime deletedAt;

  protected BaseEntity() {
    this.createdBy = String.valueOf(TokenUser.getId());
    this.createdAt = LocalDateTime.now();
  }

  public void update() {
    this.updatedBy = String.valueOf(TokenUser.getId());
    this.updatedAt = LocalDateTime.now();
  }

  public void delete() {
    update();
    this.deletedAt = LocalDateTime.now();
  }
}
