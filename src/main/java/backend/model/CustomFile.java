package backend.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;
    private String extension;

    // Optional relationship with Apelacion
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    private Apelacion apelacion;

    public CustomFile(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }
    
}
