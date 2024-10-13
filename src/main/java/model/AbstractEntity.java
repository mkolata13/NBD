package model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@MappedSuperclass
@Access(AccessType.FIELD)
@Embeddable
@Getter
public abstract class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Version
    protected Long version;
}
