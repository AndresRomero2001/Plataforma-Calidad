package backend.model;

import javax.persistence.Entity;
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
public class ReclamacionFile {

    public ReclamacionFile(Reclamacion reclamacion, String fileClass) {
        this.reclamacion = reclamacion;
        this.fileClass = fileClass;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;


    private String fileClass;

    @OneToOne
    private CustomFile archivo;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Reclamacion reclamacion;
}
