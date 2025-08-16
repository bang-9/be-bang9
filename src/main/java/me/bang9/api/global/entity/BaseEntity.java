package me.bang9.api.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import me.bang9.api.global.api.code.status.CommonErrorStatus;
import me.bang9.api.global.api.exception.Bang9Exception;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @Column(nullable = false)
    protected Boolean status = Boolean.TRUE;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    protected LocalDateTime modifiedAt = LocalDateTime.now();

    public void softDelete() {
        if (status == false)
            throw new Bang9Exception(CommonErrorStatus.ALREADY_DELETED);
        this.status = Boolean.FALSE;
    }

    public void restoreDeleted() {
        this.status = Boolean.TRUE;
    }
}