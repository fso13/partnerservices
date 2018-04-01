package ru.drudenko.partnerservices.domain;

import ru.drudenko.partnerservices.controllers.Avatar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Arrays;

@Entity
@SequenceGenerator(name = "create_seq", sequenceName = "seq_entity", allocationSize = 1)
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "create_seq")
    @Column(name = "id")
    private Long id;
    @Basic(optional = false, fetch = FetchType.LAZY)
    private byte[] photo;
    @Basic(optional = false)
    @Column(name = "mimetype")
    private String MIMEType;

    public Photo() {
    }

    public Photo(byte[] photo, String MIMEType) {
        this.photo = photo.clone();
        this.MIMEType = MIMEType;
    }

    public Long getId() {
        return id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getMIMEType() {
        return MIMEType;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", photo=" + Arrays.toString(photo) +
                ", MIMEType='" + MIMEType + '\'' +
                '}';
    }

    public Avatar toAvatar() {
        return new Avatar(id, photo.clone(), MIMEType);
    }

    public void update(byte[] bytes, String contentType) {
        photo = bytes.clone();
        MIMEType = contentType;
    }
}
