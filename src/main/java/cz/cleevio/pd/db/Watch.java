package cz.cleevio.pd.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class Watch {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private int price; // int?
    private String description;

    @Lob
    private byte [] fountain;

}
