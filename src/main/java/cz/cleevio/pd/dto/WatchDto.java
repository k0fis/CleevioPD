package cz.cleevio.pd.dto;

import lombok.*;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor@ToString
public class WatchDto {
    private String title;
    private int price; // int?
    private String description;
    private String fountain;

}
